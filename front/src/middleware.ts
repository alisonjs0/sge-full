import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { jwtDecode } from "jwt-decode";

const publicRoutes = ["/login"];

const protectedRoutes = [
  "/",
  "/dashboard",
  "/extintores",
  "/inspecoes",
  "/manutencoes",
  "/alertas",
  "/relatorios",
];

const isTokenValid = (token: string): boolean => {
  try {
    const decodedToken = jwtDecode<any>(token);
    
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

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Verifica se existe token e se ele é válido
  const token = request.cookies.get("authToken")?.value || 
                request.headers.get("authorization")?.replace("Bearer ", "");
  
  const isAuthenticated = token ? isTokenValid(token) : false;
  const isPublicRoute = publicRoutes.includes(pathname);
  const isProtectedRoute = protectedRoutes.some((route) =>
    pathname.startsWith(route)
  );

  if (isProtectedRoute && !isAuthenticated && !isPublicRoute) {
    const reason = token ? "token expired" : "no token found";
    console.log(`Redirecting to login from ${pathname} - ${reason}`);
    
    if (token && !isTokenValid(token)) {
      const response = NextResponse.redirect(new URL("/login", request.url));
      response.cookies.delete("authToken");
      return response;
    }
    
    const loginUrl = new URL("/login", request.url);
    loginUrl.searchParams.set("redirect", pathname);
    return NextResponse.redirect(loginUrl);
  }

  // Se está autenticado e tenta acessar login, redireciona para dashboard
  if (isAuthenticated && (pathname === "/login")) {
    console.log("User authenticated, redirecting to dashboard");
    return NextResponse.redirect(new URL("/dashboard", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
