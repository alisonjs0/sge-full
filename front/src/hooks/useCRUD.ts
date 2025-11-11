
import { useState, useEffect, useCallback } from 'react'
import toast from 'react-hot-toast'
import api from '../api/axios/axios'
import authService from '../service/authService'

// Mapeamento de nomes de entidades para endpoints da API
const ENTITY_ENDPOINTS: Record<string, string> = {
  'extinguisher': '/extinguisher',
  'extintor': '/extinguisher',
  'user': '/users',
  'users': '/users',
  'usuario': '/users', 
  'alert': '/alert',
  'alerta': '/alert',
  'inspections': '/inspections',
  'inspection': '/inspections',
  'inspecao': '/inspections',
  'maintenance': '/maintenance',
  'manutencao': '/maintenance',
  'equipment': '/equipment',
  'equipamento': '/equipment',
  'unit': '/unit',
  'unidade': '/unit'
}

// Mapeamento de nomes de entidades para português nas mensagens
const ENTITY_NAMES_PT: Record<string, string> = {
  'extinguisher': 'extintor',
  'extintor': 'extintor',
  'user': 'usuário',
  'users': 'usuário',
  'usuario': 'usuário', 
  'alert': 'alerta',
  'alerta': 'alerta',
  'inspections': 'inspeção',
  'inspection': 'inspeção',
  'inspecao': 'inspeção',
  'maintenance': 'manutenção',
  'manutencao': 'manutenção',
  'equipment': 'equipamento',
  'equipamento': 'equipamento',
  'unit': 'unidade',
  'unidade': 'unidade'
}

export function useCRUD<T extends { id?: number | string; _id?: string }>(entityName: string) {
  const [data, setData] = useState<T[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Pega o endpoint correto baseado no nome da entidade - memoizado
  const getEndpoint = useCallback(() => {
    return ENTITY_ENDPOINTS[entityName.toLowerCase()] || `/${entityName}`
  }, [entityName])
  
  // Pega o nome em português para as mensagens - memoizado
  const getEntityNamePT = useCallback(() => {
    return ENTITY_NAMES_PT[entityName.toLowerCase()] || entityName
  }, [entityName])

  const fetchData = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const endpoint = getEndpoint()
      
      // Verificar token antes de fazer a requisição
      const token = authService.getToken();
      if (!token) {
        console.log('❌ FETCH Data - Token ausente, não é possível buscar dados');
        const errorMsg = 'Sessão expirada. Faça login novamente.';
        setError(errorMsg);
        toast.error(errorMsg);
        
        // Redirecionar para login
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        return;
      }
      
      console.log('🔄 FETCH Data - Endpoint:', endpoint);
      console.log('🔄 FETCH Data - Chamado por:', new Error().stack?.split('\n')[2]); // Ver quem chamou
      console.log('🔄 FETCH Data - Token presente:', token ? 'SIM' : 'NÃO');
      
      // Decodificar token para verificar roles
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('🔍 Token Info:', {
          email: payload.sub,
          roles: payload.roles,
          expiresAt: new Date(payload.exp * 1000).toLocaleString(),
          isExpired: payload.exp < Date.now() / 1000
        });
        
        // Verificar se o token expirou
        if (payload.exp < Date.now() / 1000) {
          console.log('❌ Token EXPIRADO! Redirecionando para login...');
          authService.logout();
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
          return;
        }
        
        // Verificar se tem roles
        if (!payload.roles || payload.roles === 'null' || payload.roles === '') {
          console.log('⚠️ ATENÇÃO: Usuário sem ROLES definidas!');
          console.log('O backend requer que o usuário tenha role "USER" ou "ADMIN"');
          toast.error('Seu usuário não tem permissões. Entre em contato com o administrador.');
        }
      } catch (e) {
        console.log('❌ Erro ao decodificar token:', e);
      }
      
      const response = await api.get(endpoint)
      
      // Spring Boot retorna os dados diretamente no response.data
      const responseData = Array.isArray(response.data) ? response.data : []
      console.log('✅ FETCH Data - Recebidos:', responseData.length, 'registros');
      setData(responseData)
    } catch (err: any) {
      const errorMessage = `Erro ao carregar ${getEntityNamePT()}: ${err.response?.data?.message || err.message || 'Erro desconhecido'}`
      setError(errorMessage)
      console.log('❌', errorMessage, err)
      
      // Se for 403, pode ser problema de autenticação/autorização
      if (err.response?.status === 403) {
        console.log('❌ ERRO 403 - ACESSO NEGADO');
        console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        console.log('📋 Possíveis causas:');
        console.log('  1. Usuário sem roles definidas no banco de dados');
        console.log('  2. Usuário tem role incorreta (deve ser "USER" ou "ADMIN")');
        console.log('  3. Token inválido ou mal formatado');
        console.log('  4. Backend rejeitando a requisição');
        console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        console.log('💡 Solução:');
        console.log('  1. Verifique no banco se o usuário tem a coluna "roles" preenchida');
        console.log('  2. A role deve ser "USER", "ADMIN" ou "ROLE_USER", "ROLE_ADMIN"');
        console.log('  3. Faça logout e login novamente');
        console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
        
        // toast.error(
        //   'Acesso negado! Seu usuário pode não ter as permissões necessárias. Verifique com o administrador.',
        //   { duration: 8000 }
        // );
        console.log('⚠️ Detalhes do erro:', err.response?.data);
        
        // Verificar se o token ainda é válido
        const token = authService.getToken();
        if (!token) {
          const authErrorMsg = 'Sua sessão expirou. Por favor, faça login novamente.';
          toast.error(authErrorMsg);
          setTimeout(() => {
            if (typeof window !== 'undefined') {
              window.location.href = '/login';
            }
          }, 1500);
        } else {
          // toast.error('Você não tem permissão para acessar estes dados. Verifique suas credenciais.');
        }
      } else {
        toast.error(errorMessage)
      }
    } finally {
      setLoading(false)
    }
  }, [entityName, getEndpoint, getEntityNamePT])

  const createRecord = async (recordData: Omit<T, 'id' | '_id'>) => {
    try {
      setLoading(true)
      
      // Verificar se há token válido antes de prosseguir
      const token = authService.getToken();
      if (!token) {
        const errorMsg = 'Sessão expirada. Faça login novamente.';
        console.log('❌ CREATE - Token ausente');
        toast.error(errorMsg);
        authService.logout();
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        throw new Error(errorMsg);
      }
      
      console.log('📝 CREATE Record - Dados recebidos:', recordData);
      
      // Processa os dados para garantir tipos corretos
      const processedData = Object.entries(recordData).reduce((acc, [key, value]) => {
        if (typeof value === 'string' && /^\d+(\.\d+)?$/.test(value)) {
          acc[key] = parseFloat(value)
        }
        else if (value === "true" || value === "false") {
          acc[key] = value === "true"
        }
        else {
          acc[key] = value
        }
        return acc
      }, {} as any)

      console.log('📝 CREATE Record - Dados processados:', processedData);
      
      const endpoint = getEndpoint()
      console.log('📝 CREATE Record - Endpoint:', endpoint);
      
      const response = await api.post(endpoint, processedData)
      
      // Adiciona o novo registro ao estado
      const newRecord = response.data
      setData(prev => [newRecord, ...prev])
      
      toast.success(`${getEntityNamePT()} criado com sucesso!`)
      return newRecord
    } catch (err: any) {
      const errorMessage = `Erro ao criar ${getEntityNamePT()}: ${err.response?.data?.message || err.message || 'Erro desconhecido'}`
      console.log(errorMessage, err)
      console.log('Erro completo:', {
        status: err.response?.status,
        data: err.response?.data,
        headers: err.response?.headers
      });
      toast.error(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const updateRecord = async (id: string | number, updates: Partial<T>) => {
    if (!id && id !== 0) {
      throw new Error('ID é obrigatório')
    }

    try {
      setLoading(true)
      
      // Verificar se há token válido antes de prosseguir
      const token = authService.getToken();
      if (!token) {
        const errorMsg = 'Sessão expirada. Faça login novamente.';
        console.log('❌ UPDATE - Token ausente');
        toast.error(errorMsg);
        authService.logout();
        if (typeof window !== 'undefined') {
          window.location.href = '/login';
        }
        throw new Error(errorMsg);
      }
      
      console.log('✏️ UPDATE Record - ID:', id);
      console.log('✏️ UPDATE Record - Dados recebidos:', updates);
      
      const endpoint = getEndpoint()
      console.log('✏️ UPDATE Record - Endpoint:', `${endpoint}/${id}`);
      
      // Fazer o PUT request
      const response = await api.put(`${endpoint}/${id}`, updates)
      
      console.log('✅ UPDATE Record - Resposta recebida:', response.status);
      console.log('✅ UPDATE Record - Dados:', response.data);
      
      // Verificar se a resposta foi bem-sucedida
      if (response.status === 200 || response.status === 201) {
        const updatedRecord = response.data;
        
        // Atualizar o estado local APENAS com os dados retornados
        setData(prev => prev.map(item => {
          const itemId = item.id || item._id;
          return itemId == id ? updatedRecord : item;
        }));
        
        console.log('✅ UPDATE Record - Estado local atualizado com sucesso');
        toast.success(`${getEntityNamePT()} atualizado com sucesso!`);
        
        return updatedRecord;
      } else {
        throw new Error(`Resposta inesperada do servidor: ${response.status}`);
      }
    } catch (err: any) {
      console.log('❌ UPDATE Record - Erro capturado:', err);
      console.log('❌ UPDATE Record - Status:', err.response?.status);
      console.log('❌ UPDATE Record - Data:', err.response?.data);

      const errorMessage = `Erro ao atualizar ${getEntityNamePT()}: ${err.response?.data?.message || err.message || 'Erro desconhecido'}`;
      console.log(errorMessage);
      
      // Mostrar toast apenas para erros reais (não 403 se for após sucesso)
      if (err.response?.status !== 403 || !err.response?.data) {
        toast.error(errorMessage);
      }
      
      throw err;
    } finally {
      setLoading(false);
    }
  }

  const deleteRecord = async (id: string | number) => {
    if (!id && id !== 0) {
      throw new Error('ID é obrigatório')
    }

    try {
      setLoading(true)
      
      const endpoint = getEndpoint()
      await api.delete(`${endpoint}/${id}`)
      
      setData(prev => prev.filter(item => {
        const itemId = item.id || item._id
        return itemId != id
      }))
      
      toast.success(`${getEntityNamePT()} excluído com sucesso!`)
    } catch (err: any) {
      const errorMessage = `Erro ao excluir ${getEntityNamePT()}: ${err.response?.data?.message || err.message || 'Erro desconhecido'}`
      console.log(errorMessage, err)
      toast.error(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const deleteMultipleRecords = async (ids: (string | number)[]) => {
    if (!Array.isArray(ids) || ids.length === 0) {
      throw new Error('Lista de IDs não pode estar vazia')
    }

    try {
      setLoading(true)
      
      // Como o Spring Boot não tem endpoint para deletar múltiplos,
      // fazemos uma requisição para cada ID
      const endpoint = getEndpoint()
      const deletePromises = ids.map(id => api.delete(`${endpoint}/${id}`))
      
      await Promise.all(deletePromises)
      
      setData(prev => prev.filter(item => {
        const itemId = item.id || item._id
        return !ids.includes(itemId!)
      }))
      
      toast.success(`${ids.length} ${getEntityNamePT()}(s) excluído(s) com sucesso!`)
    } catch (err: any) {
      const errorMessage = `Erro ao excluir ${getEntityNamePT()}s: ${err.response?.data?.message || err.message || 'Erro desconhecido'}`
      console.log(errorMessage, err)
      toast.error(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [fetchData])

  return {
    data,
    loading,
    error,
    fetchData,
    createRecord,
    updateRecord,
    deleteRecord,
    deleteMultipleRecords
  }
}
