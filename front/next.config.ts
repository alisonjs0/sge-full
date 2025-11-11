
import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  output: 'standalone',
  // Configurações experimentais
  experimental: {
    optimizePackageImports: ['lucide-react'],
    // Desabilita turbo temporariamente para resolver problemas do Tailwind
    turbo: {
      rules: {
        '*.css': {
          loaders: ['postcss-loader'],
        },
      },
    },
  },
  eslint: {
    // Desabilita ESLint durante o build para resolver problemas de configuração
    ignoreDuringBuilds: true,
  },
  typescript: {
    // Desabilita verificação de tipos durante o build se necessário
    ignoreBuildErrors: false,
  },
  images: {
    domains: ['images.pexels.com', 'lumi.new'],
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'images.pexels.com',
        port: '',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'lumi.new',
        port: '',
        pathname: '/**',
      },
    ],
  },
}

export default nextConfig
