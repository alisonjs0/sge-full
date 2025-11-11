export interface IUser {
    id: number;
    cpf: string;
    nome?: string;  // Backend usa "nome" em português
    email?: string;
    roles?: string[];
}