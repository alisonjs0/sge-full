// ===== TIPOS SPRING BOOT (NOVOS) =====
export interface User {
  id?: number
  nome: string  // Backend usa "nome" em português
  email: string
  cpf: string
  senha?: string  // Backend usa "senha" em português
  roles: string
  cargo: string  // Backend usa "cargo" em português
  createdDate?: string
}

export interface Unidade {
  id: number
  nome: string
  descricao?: string
  responsavel?: {
    id: number
    nome: string  // Backend usa "nome" em português
    email: string
  }
  ativo: boolean
  createdAt: string
  updatedAt: string
}

export interface Extinguisher {
  id?: number;
  numeroIdentificacao: string;
  localizacao: string;
  tipoAgente: string; // Backend usa nome em português
  classeIncendio: string; // Backend usa nome em português
  capacidade: string;
  validade: Date | string; // Backend usa nome em português - Aceita Date (frontend) ou string ISO (backend)
  dataFabricacao: Date | string; // Backend usa nome em português - Aceita Date (frontend) ou string ISO (backend)
  fabricante: string;
  status: string;
  observacoes: string;
  unidadeId: number;
}

export interface Alert {
  id: number
  tipoAlerta: string
  mensagem: string
  prioridade: 'baixa' | 'media' | 'alta' | 'critica'
  status: 'pendente' | 'lido' | 'resolvido'
  extintor?: {
    id: number
    numeroIdentificacao: string
    localizacao: string
  }
  usuario?: {
    id: number
    nome: string
    email: string
  }
  dataCriacao: string
  dataLeitura?: string
  dataResolucao?: string
}

export interface Inspection {
  id?: number
  inspectionDate: string | Date
  inspectionAuthor?: { id: number } | number
  extinguisher?: { id: number } | number
  manometro: boolean
  seal: boolean
  rotulo: boolean
  damages: boolean
  obstructions: boolean
  sinalizacao: boolean
  suporteFixacao: boolean
  observations?: string
  nextInspectionDate?: string | Date
}

export interface Maintenance {
  id: number;
  extinguisher?: {
    id: number;
    numeroIdentificacao: string;
    localizacao: string;
  };
  tipo: "preventiva" | "corretiva" | "recarga" | "teste_hidrostatico";
  status: "agendada" | "em_andamento" | "concluida" | "cancelada";
  dataAgendada: string;
  dataRealizada?: string;
  responsavel?: {
    id: number;
    nome: string;
    email: string;
  };
  descricao: string;
  custo?: number;
  observacoes?: string;
  proximaManutencao?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Extintor {
  _id: string;
  numeroIdentificacao: string;
  unidadeId: string;
  localizacao: string;
  tipoAgente: "po_abc" | "co2" | "agua" | "espuma" | "po_quimico";
  classeIncendio: "A" | "B" | "C" | "AB" | "BC" | "ABC";
  capacidade: string;
  dataFabricacao: string;
  dataValidade: string;
  fabricante: string;
  status: "conforme" | "nao_conforme" | "vencido" | "manutencao";
  qrCode?: string;
  codigoBarras?: string;
  observacoes?: string;
  creator: string;
  createdAt: string;
  updatedAt: string;
}

export interface Checklist {
  manometro: boolean;
  rotulo: boolean;
  lacre: boolean;
  sinalizacao: boolean;
  danos: boolean;
  obstrucoes: boolean;
  fixacao: boolean;
  validade: boolean;
}

export interface Inspecao {
  _id: string;
  extintorId: string;
  inspetorId: string;
  dataInspecao: string;
  resultado: "conforme" | "nao_conforme";
  checklist: Checklist;
  observacoes?: string;
  fotos?: string[];
  proximaInspecao?: string;
  creator: string;
  createdAt: string;
  updatedAt: string;
}

export interface Manutencao {
  id: number;
  extinguisher?: {
    id: number;
    numeroIdentificacao: string;
    localizacao: string;
  };
  tipo: "preventiva" | "corretiva" | "recarga" | "teste_hidrostatico";
  status: "agendada" | "em_andamento" | "concluida" | "cancelada";
  dataAgendada: string;
  dataRealizada?: string;
  responsavel?: {
    id: number;
    nome: string;
    email: string;
  };
  descricao: string;
  custo?: number;
  observacoes?: string;
  proximaManutencao?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Usuario {
  _id: string;
  nome: string;
  email: string;
  perfil: "administrador" | "tecnico" | "estagiario_tecnico";
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  totalExtintores: number
  extintoresConformes: number
  extintoresVencidos: number
  inspecoesMes: number
  manutencoesPendentes: number
}
