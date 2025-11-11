import { jwtDecode, JwtPayload } from "jwt-decode";
import api from "../api/axios/axios";

const TOKEN_KEY = "authToken";
const COOKIE_KEY = "authToken";
const DEFAULT_COOKIE_MAX_AGE_DAYS = 7;

interface MyCustomClaims {
  id: number | null,
  name: string | null,
  roles: string | null
}

type MyTokenPayload = JwtPayload & MyCustomClaims;

interface LoginResponse {
  token: string;
  user?: any;
  message?: string;
}

const setCookie = (name: string, value: string, days = DEFAULT_COOKIE_MAX_AGE_DAYS) => {
  if (typeof document === "undefined") return;

  const expires = new Date();
  expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);

  document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/;SameSite=Lax`;
};

const removeCookie = (name: string) => {
  if (typeof document === "undefined") return;

  document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;SameSite=Lax`;
};

const getCookie = (name: string): string | null => {
  if (typeof document === "undefined") return null;

  const match = document.cookie.match(new RegExp(`(^| )${name}=([^;]+)`));
  return match ? decodeURIComponent(match[2]) : null;
};

const persistToken = (token: string) => {
  if (typeof window !== "undefined") {
    localStorage.setItem(TOKEN_KEY, token);
  }

  setCookie(COOKIE_KEY, token);

  api.defaults.headers.common.Authorization = `Bearer ${token}`;
};

const clearPersistedToken = () => {
  if (typeof window !== "undefined") {
    localStorage.removeItem(TOKEN_KEY);
  }

  removeCookie(COOKIE_KEY);

  if (api.defaults.headers.common?.Authorization) {
    delete api.defaults.headers.common.Authorization;
  }
};

const login = async (email: any, password: any): Promise<LoginResponse> => {
  console.log("Enviando para login:", email, password);
  const response = await api.post("/auth/login", {
    email: email,
    password: password,
  }, { headers: { "Content-Type": "application/json" } });

  if (response.data.token) {
    persistToken(response.data.token);
  }

  return response.data;
};

const logout = () => {
  clearPersistedToken();
};

const getToken = () => {
  let token: string | null = null;

  if (typeof window !== "undefined") {
    token = localStorage.getItem(TOKEN_KEY);
  }

  if (!token) {
    token = getCookie(COOKIE_KEY);
  }

  if (!token) {
    clearPersistedToken();
    return null;
  }

  if (!isTokenValid(token)) {
    console.log("Token inválido ou expirado, limpando credenciais");
    clearPersistedToken();
    return null;
  }

  persistToken(token);
  return token;
};

const isTokenValid = (token: string): boolean => {
  try {
    const decodedToken = jwtDecode<MyTokenPayload>(token);
    
    if (!decodedToken.exp) {
      return false;
    }
    
    // Verifica se o token expirou (exp está em segundos, Date.now() em milissegundos)
    const currentTime = Date.now() / 1000;
    return decodedToken.exp > currentTime;
  } catch (error) {
    return false;
  }
};

const getUser = () => {
  const token = getToken();
  if (!token) {
    return null;
  }

  try {
    // Primeiro verifica se o token é válido e não expirou
    if (!isTokenValid(token)) {
      console.log("Token expirado, fazendo logout automático");
      logout();
      return null;
    }

    const decodedToken = jwtDecode<MyTokenPayload>(token);

    return {
      id: decodedToken.id,
      cpf: decodedToken.sub,
      name: decodedToken.name || undefined,
      roles: decodedToken.roles || undefined
    };
  } catch (error) {
    console.error("Token invalido ou expirado", error);
    logout();
    return null;
  }
};

const authService = {
  login,
  logout,
  getToken,
  getUser,
  isTokenValid
};

export default authService;