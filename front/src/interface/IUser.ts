export interface IUser {
    id: number;
    cpf: string;
    nome?: string;  // Backend usa "nome" em português
    name?: string;  // Frontend usa "name" em inglês
    email?: string;
    roles?: string[];
}