import axios from "axios";
import authService from "../../service/authService";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8081"
});

api.interceptors.request.use((config) => {
  // Skip autenticação apenas para rota de login
  if (config.url === "/auth/login") {
    return config;
  }

  const token = authService.getToken();

  if (!token) {
    console.log('⚠️ Token ausente! Não é possível fazer requisições autenticadas.');
    console.log('⚠️ URL tentada:', config.url);
    console.log('⚠️ Método:', config.method);

    // Logout e redirecionar
    authService.logout();

    if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
      console.log('⚠️ Redirecionando para login...');
      window.location.href = '/login';
    }

    return Promise.reject(new Error('Token de autenticação ausente'));
  }

  // Adicionar token aos headers
  config.headers = config.headers || {};
  (config.headers as any).Authorization = `Bearer ${token}`;
  
  // Log detalhado para todas as requisições (especialmente GET para alertas)
  if (config.url?.includes('/alert') || config.method?.toUpperCase() === 'POST' || config.method?.toUpperCase() === 'PUT') {
    console.log(`🔍 ${config.method?.toUpperCase()} Request Debug:`, {
      url: config.url,
      method: config.method,
      hasToken: !!token,
      tokenPreview: token ? `${token.substring(0, 20)}...` : null,
      authHeader: config.headers.Authorization ? 'PRESENTE' : 'AUSENTE',
      dataKeys: config.data ? Object.keys(config.data) : []
    });
    
    // Decodificar e mostrar informações do token
    try {
      const tokenPayload = JSON.parse(atob(token.split('.')[1]));
      console.log('🔍 Token Payload:', {
        sub: tokenPayload.sub,
        roles: tokenPayload.roles,
        exp: tokenPayload.exp,
        expiresAt: new Date(tokenPayload.exp * 1000).toLocaleString(),
        isExpired: tokenPayload.exp < Date.now() / 1000
      });
    } catch (e) {
      console.error('❌ Erro ao decodificar token:', e);
    }
  }
  
  return config;
});

// Interceptor para tratar erros de resposta e avisar sobre 401/403
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    const url = error?.config?.url;
    const method = error?.config?.method?.toUpperCase();
    
    console.log('❌ Erro na requisição:', {
      method,
      status,
      url,
      responseData: error?.response?.data,
      requestData: error?.config?.data,
      hasAuthHeader: !!error?.config?.headers?.Authorization,
      authHeaderPreview: error?.config?.headers?.Authorization 
        ? `${error?.config?.headers?.Authorization.substring(0, 30)}...` 
        : null
    });
    
    if (status === 401) {
      console.error('Erro 401: não autorizado. Credenciais inválidas ou token expirado.');
      
      // Se não é tentativa de login, pode ser token expirado
      if (url !== '/auth/login') {
        console.log('Token possivelmente expirado, removendo do storage');
        authService.logout();
        if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
          // Redireciona para login
          window.location.href = '/login';
        }
      }
    } else if (status === 403) {
      console.log('❌ Erro 403: acesso negado.');
      console.log('Possíveis causas:');
      console.log('- Token ausente ou inválido');
      console.log('- Usuário sem permissão para esta operação');
      console.log('- Backend rejeitando o formato dos dados');
      console.log('- CORS ou problema de autenticação');
    }
    return Promise.reject(error);
  }
);

export default api;