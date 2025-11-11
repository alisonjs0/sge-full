"use client";

import React, { useState } from 'react'
import {Plus, Search, Edit, Trash2, Building2, MapPin, User} from 'lucide-react'
import { useCRUD } from '@/hooks/useCRUD'
import { Unidade, User as UserType } from '@/types'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import { motion } from 'framer-motion'

import MainHeader from '@/components/MainHeader';

const Unidades: React.FC = () => {
  const { data: unidades, loading, createRecord, updateRecord, deleteRecord } = useCRUD<Unidade>('unit')
  const { data: usuarios } = useCRUD<UserType>('users')
  const [showForm, setShowForm] = useState(false)
  const [editingUnidade, setEditingUnidade] = useState<Unidade | null>(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [showModal, setShowModal] = useState(false);

  const filteredUnidades = unidades.filter(unidade => {
    const matchesSearch = unidade.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         unidade.descricao?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         unidade.responsavel?.nome?.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesStatus = !statusFilter || 
                         (statusFilter === 'ativo' && unidade.ativo) ||
                         (statusFilter === 'inativo' && !unidade.ativo)
    return matchesSearch && matchesStatus
  })

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)
    
    const unidadeData: any = {
      nome: formData.get('nome') as string,
      descricao: formData.get('descricao') as string,
      ativo: formData.get('ativo') === 'true'
    }

    const responsavelId = formData.get('responsavelId') as string
    if (responsavelId) {
      unidadeData.responsavelId = Number(responsavelId)
    }

    try {
      if (editingUnidade) {
        await updateRecord(editingUnidade.id, unidadeData)
      } else {
        await createRecord(unidadeData)
      }
      setShowForm(false)
      setEditingUnidade(null)
    } catch (error) {
      console.error('Erro ao salvar unidade:', error)
    }
  }

  const handleEdit = (unidade: Unidade) => {
    setEditingUnidade(unidade)
    setShowForm(true)
  }

  const handleDelete = async (unidade: Unidade) => {
    if (confirm(`Tem certeza que deseja excluir a unidade "${unidade.nome}"?`)) {
      await deleteRecord(unidade.id)
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-red-600"></div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        <MainHeader
          icon={<MapPin className="text-blue-600" />}
          textHeader="Unidade"
          subtitle="Gerencie as unidades organizacionais do sistema"
          showButton={true}
          buttonText="Nova Unidade"
          buttonIcon={<Plus size={20} />}
          onButtonClick={() => { setEditingUnidade(null); setShowForm(true) }}
        />

      </div>

      {/* Filtros */}
      <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-xl shadow-sm p-6 mb-6"
        >
      <div className="bg-white p-4 rounded-lg shadow">
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nome, descrição ou responsável..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
          >
            <option value="">Todos os status</option>
            <option value="ativo">Ativo</option>
            <option value="inativo">Inativo</option>
          </select>
          <div className="text-sm text-gray-500 flex items-center">
            Total: {filteredUnidades.length} unidade(s)
          </div>
        </div>
      </div>
      </motion.div>

      {/* Lista de unidades */}
      <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-xl shadow-sm p-6 mb-6"
        >
      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        <ul className="divide-y divide-gray-200">
          {filteredUnidades.map((unidade) => (
            <li key={unidade.id}>
              <div className="px-4 py-4 flex items-center justify-between">
                <div className="flex items-center min-w-0 flex-1">
                  <div className="flex-shrink-0">
                    <Building2 className="h-10 w-10 text-red-600" />
                  </div>
                  <div className="min-w-0 flex-1 px-4">
                    <div className="flex items-center justify-between">
                      <p className="text-sm font-medium text-red-600 truncate">
                        {unidade.nome}
                      </p>
                      <div className="ml-2 flex-shrink-0">
                        <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                          unidade.ativo 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {unidade.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                      </div>
                    </div>
                    <div className="mt-2">
                      {unidade.descricao && (
                        <div className="flex items-center text-sm text-gray-500 mb-1">
                          <MapPin className="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400" />
                          <p className="truncate">{unidade.descricao}</p>
                        </div>
                      )}
                      <div className="flex items-center justify-between">
                        <div className="flex items-center text-sm text-gray-500">
                          {unidade.responsavel && (
                            <>
                              <User className="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400" />
                              <p className="truncate">{unidade.responsavel.nome}</p>
                            </>
                          )}
                        </div>
                        <p className="text-xs text-gray-400">
                          Criado em {format(new Date(unidade.createdAt), 'dd/MM/yyyy', { locale: ptBR })}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleEdit(unidade)}
                    className="text-indigo-600 hover:text-indigo-900"
                  >
                    <Edit className="h-5 w-5" />
                  </button>
                  <button
                    onClick={() => handleDelete(unidade)}
                    className="text-red-600 hover:text-red-900"
                  >
                    <Trash2 className="h-5 w-5" />
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
        {filteredUnidades.length === 0 && (
          <div className="px-4 py-12 text-center">
            <Building2 className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhuma unidade encontrada</h3>
            <p className="mt-1 text-sm text-gray-500">
              {searchTerm || statusFilter ? 'Tente ajustar os filtros de busca.' : 'Comece cadastrando uma nova unidade.'}
            </p>
          </div>
        )}
      </div>
      </motion.div>

      {/* Modal de formulário */}
      {showForm && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-11/12 max-w-lg shadow-lg rounded-md bg-white">
            <h3 className="text-lg font-bold text-gray-900 mb-4">
              {editingUnidade ? 'Editar Unidade' : 'Nova Unidade'}
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Nome da Unidade *
                </label>
                <input
                  name="nome"
                  type="text"
                  defaultValue={editingUnidade?.nome || ''}
                  required
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="Ex: Matriz, Filial Centro, Depósito Norte"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Descrição
                </label>
                <textarea
                  name="descricao"
                  rows={3}
                  defaultValue={editingUnidade?.descricao || ''}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
                  placeholder="Descrição detalhada da unidade, localização, características..."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Responsável
                </label>
                <select
                  name="responsavelId"
                  defaultValue={editingUnidade?.responsavel?.id || ''}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
                >
                  <option value="">Selecione um responsável</option>
                  {usuarios.map((usuario) => (
                    <option key={usuario.id} value={usuario.id}>
                      {usuario.nome}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Status *
                </label>
                <select
                  name="ativo"
                  defaultValue={editingUnidade?.ativo !== null && editingUnidade?.ativo !== undefined ? String(editingUnidade.ativo) : 'true'}
                  required
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
                >
                  <option value="true">Ativo</option>
                  <option value="false">Inativo</option>
                </select>
              </div>

              <div className="flex justify-end space-x-3 pt-4">
                <button
                  type="button"
                  onClick={() => { setShowForm(false); setEditingUnidade(null) }}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700"
                >
                  {editingUnidade ? 'Atualizar' : 'Cadastrar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Unidades
