"use client";

import React, { createContext, useState, useContext, useEffect, ReactNode, useCallback } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import authService from '../service/authService';
import { IUser } from "../interface/IUser";
import LoadingSpinner from '../components/LoadingSpinner';

interface AuthContextType {
  isAuthenticated: boolean;
  user: IUser | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export { AuthContext };

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<IUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);

    if (pathname !== '/login') {
      router.push('/login');
    }
  }, [pathname, router]);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const token = authService.getToken();
        if (!token) {
          logout();
          return;
        }

        // Usa dados do token JWT para definir o usuário
        const tokenUser = authService.getUser();
        if (tokenUser) {
          setUser(tokenUser as IUser);
        } else {
          logout();
        }
      } catch (error) {
        console.error("Erro ao verificar autenticação:", error);
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();

    // Verifica o token a cada minuto para detectar expiração
    const interval = setInterval(() => {
      const token = authService.getToken();
      if (!token) {
        console.log("Token ausente, fazendo logout");
        logout();
        return;
      }

      if (!authService.isTokenValid(token)) {
        console.log("Token expirado detectado, fazendo logout");
        logout();
      }
    }, 60000); // 1 minuto

    return () => clearInterval(interval);
  }, [logout]);

  const login = async (email: string, password: string) => {
    try {
      const response = await authService.login(email, password);
      
      // Salva o token também em cookie para o middleware
      if (response.token) {
        // Define um usuário básico baseado no token JWT
        const tokenUser = authService.getUser();
        setUser(tokenUser as IUser);
        
        // Redireciona para o dashboard após login
        router.push('/dashboard');
      } else {
        throw new Error('Token de autenticação não retornado pelo servidor.');
      }
    } catch (error) {
      throw error;
    }
  };

  const value = {
    isAuthenticated: !!user,
    user,
    isLoading,
    login,
    logout,
  };

  // Mostra loading global enquanto verifica autenticação
  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#E6FFFA] flex items-center justify-center">
        <LoadingSpinner size="lg" text="Verificando autenticação..." />
      </div>
    );
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};