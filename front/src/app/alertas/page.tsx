'use client'

import React, { useState, useMemo, useEffect } from 'react'
import { motion } from 'framer-motion'
import { format, parseISO } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import {AlertTriangle, Clock, CheckCircle, XCircle, Bell, Filter, Search, Eye, Trash2, Plus, AlertCircle, Info} from 'lucide-react'
import { useCRUD } from '../../hooks/useCRUD'
import { Alert, Extinguisher } from '../../types'
import MainHeader from '@/components/MainHeader'
import api from '@/api/axios/axios'
import toast from 'react-hot-toast'
import authService from '@/service/authService'

const Alertas: React.FC = () => {
  const { 
    data: alertas, 
    loading, 
    error,
    createRecord, 
    updateRecord, 
    deleteRecord,
    fetchData 
  } = useCRUD<Alert>('alert')
  
  const { data: extintores } = useCRUD<Extinguisher>('extinguisher')
  
  // Verificar autenticação ao montar o componente
  useEffect(() => {
    const token = authService.getToken();
    if (!token) {
      console.log('❌ Alertas Page - Token ausente, redirecionando...');
      toast.error('Sessão expirada. Faça login novamente.');
      setTimeout(() => {
        window.location.href = '/login';
      }, 1500);
    } else {
      console.log('✅ Alertas Page - Token presente');
    }
  }, [])
  
  const [searchTerm, setSearchTerm] = useState('')
  const [typeFilter, setTypeFilter] = useState<string>('todos')
  const [priorityFilter, setPriorityFilter] = useState<string>('todos')
  const [statusFilter, setStatusFilter] = useState<string>('todos')
  const [showModal, setShowModal] = useState(false)
  const [formData, setFormData] = useState({
    tipoAlerta: '',
    mensagem: '',
    prioridade: 'media' as 'baixa' | 'media' | 'alta' | 'critica',
    extintorId: ''
  })

  const filteredAlerts = useMemo(() => {
    return alertas.filter(alert => {
      const matchesSearch = alert.tipoAlerta?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          alert.mensagem?.toLowerCase().includes(searchTerm.toLowerCase())
      
      const matchesType = typeFilter === 'todos' || alert.tipoAlerta === typeFilter
      const matchesPriority = priorityFilter === 'todos' || alert.prioridade === priorityFilter
      const matchesStatus = statusFilter === 'todos' || alert.status === statusFilter
      
      return matchesSearch && matchesType && matchesPriority && matchesStatus
    })
  }, [alertas, searchTerm, typeFilter, priorityFilter, statusFilter])

  const stats = useMemo(() => {
    const total = alertas.length
    const criticos = alertas.filter(a => a.prioridade === 'critica').length
    const altos = alertas.filter(a => a.prioridade === 'alta').length
    const pendentes = alertas.filter(a => a.status === 'pendente').length
    const resolvidos = alertas.filter(a => a.status === 'resolvido').length

    return { total, criticos, altos, pendentes, resolvidos }
  }, [alertas])

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'critica': return 'bg-red-100 text-red-800 border-red-200'
      case 'alta': return 'bg-orange-100 text-orange-800 border-orange-200'
      case 'media': return 'bg-yellow-100 text-yellow-800 border-yellow-200'
      case 'baixa': return 'bg-green-100 text-green-800 border-green-200'
      default: return 'bg-gray-100 text-gray-800 border-gray-200'
    }
  }

  const getPriorityIcon = (priority: string) => {
    switch (priority) {
      case 'critica': return <AlertTriangle className="h-4 w-4" />
      case 'alta': return <AlertCircle className="h-4 w-4" />
      case 'media': return <Clock className="h-4 w-4" />
      case 'baixa': return <Info className="h-4 w-4" />
      default: return <Bell className="h-4 w-4" />
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'pendente': return 'bg-yellow-100 text-yellow-800'
      case 'lido': return 'bg-blue-100 text-blue-800'
      case 'resolvido': return 'bg-green-100 text-green-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const alertData: any = {
        tipoAlerta: formData.tipoAlerta,
        mensagem: formData.mensagem,
        prioridade: formData.prioridade,
        status: 'pendente'
      }

      if (formData.extintorId) {
        alertData.extintorId = Number(formData.extintorId)
      }

      console.log('📤 Enviando alerta:', alertData)
      
      await createRecord(alertData)
      
      console.log('✅ Alerta criado com sucesso')
      setShowModal(false)
      setFormData({
        tipoAlerta: '',
        mensagem: '',
        prioridade: 'media',
        extintorId: ''
      })
      toast.success('Alerta criado com sucesso!')
    } catch (error: any) {
      console.log('❌ Erro ao criar alerta:', error)
      console.log('❌ Resposta do servidor:', error.response?.data)
      console.log('❌ Status:', error.response?.status)
      
      const errorMsg = error.response?.data?.error || error.response?.data?.message || error.message || 'Erro desconhecido'
      toast.error(`Erro ao criar alerta: ${errorMsg}`)
    }
  }

  const markAsRead = async (id: number) => {
    try {
      await api.patch(`/alert/${id}/lido`)
      await fetchData()
      toast.success('Alerta marcado como lido')
    } catch (error) {
      console.error('Erro ao marcar como lido:', error)
      toast.error('Erro ao marcar como lido')
    }
  }

  const markAsResolved = async (id: number) => {
    try {
      await api.patch(`/alert/${id}/resolvido`)
      await fetchData()
      toast.success('Alerta marcado como resolvido')
    } catch (error) {
      console.error('Erro ao marcar como resolvido:', error)
      toast.error('Erro ao marcar como resolvido')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
      </div>
    )
  }

  // Mostrar erro se houver problema de autenticação
  if (error && error.includes('403')) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center p-8 bg-red-50 rounded-xl max-w-md">
          <AlertTriangle className="h-16 w-16 text-red-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-red-800 mb-2">Acesso Negado</h2>
          <p className="text-red-600 mb-4">
            Você não tem permissão para acessar os alertas ou sua sessão expirou.
          </p>
          <button
            onClick={() => window.location.href = '/login'}
            className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-700 transition-colors"
          >
            Fazer Login Novamente
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <MainHeader
          icon={<Bell className="w-8 h-8 text-red-600" />}
          textHeader="Alertas"
          badgeValue={stats.pendentes}
          subtitle="Monitore alertas e notificações do sistema"
          buttonIcon={<Plus size={20} />}
          buttonText='Novo Alerta'
          showButton={true}
          onButtonClick={() => setShowModal(true)}
        />
        {/* Stats Cards */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-6"
        >
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Total</p>
                <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
              </div>
              <Bell className="h-8 w-8 text-blue-600" />
            </div>
          </div>
          
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Críticos</p>
                <p className="text-2xl font-bold text-red-600">{stats.criticos}</p>
              </div>
              <AlertTriangle className="h-8 w-8 text-red-600" />
            </div>
          </div>
          
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Alta Prioridade</p>
                <p className="text-2xl font-bold text-orange-600">{stats.altos}</p>
              </div>
              <AlertCircle className="h-8 w-8 text-orange-600" />
            </div>
          </div>
          
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Pendentes</p>
                <p className="text-2xl font-bold text-yellow-600">{stats.pendentes}</p>
              </div>
              <Clock className="h-8 w-8 text-yellow-600" />
            </div>
          </div>
          
          <div className="bg-white p-6 rounded-xl shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">Resolvidos</p>
                <p className="text-2xl font-bold text-green-600">{stats.resolvidos}</p>
              </div>
              <CheckCircle className="h-8 w-8 text-green-600" />
            </div>
          </div>
        </motion.div>

        {/* Filters */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-xl shadow-sm p-6 mb-6"
        >
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Buscar alertas..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="todos">Todos os Tipos</option>
              <option value="vencimento">Vencimento</option>
              <option value="inspecao">Inspeção</option>
              <option value="manutencao">Manutenção</option>
              <option value="sistema">Sistema</option>
            </select>
            
            <select
              value={priorityFilter}
              onChange={(e) => setPriorityFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="todos">Todas as Prioridades</option>
              <option value="critica">Crítica</option>
              <option value="alta">Alta</option>
              <option value="media">Média</option>
              <option value="baixa">Baixa</option>
            </select>
            
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
            >
              <option value="todos">Todos Status</option>
              <option value="pendente">Pendentes</option>
              <option value="lido">Lidos</option>
              <option value="resolvido">Resolvidos</option>
            </select>
          </div>
        </motion.div>

        {/* Alerts List */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="space-y-4"
        >
          {filteredAlerts.length === 0 ? (
            <div className="bg-white p-12 rounded-xl shadow-sm text-center">
              <Bell className="h-16 w-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                Nenhum alerta encontrado
              </h3>
              <p className="text-gray-500">
                Não há alertas correspondentes aos filtros selecionados.
              </p>
            </div>
          ) : (
            filteredAlerts.map((alert) => (
              <motion.div
                key={alert.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold border ${getPriorityColor(alert.prioridade || 'media')}`}>
                        {getPriorityIcon(alert.prioridade || 'media')}
                        {(alert.prioridade || 'media').toUpperCase()}
                      </span>
                      <span className={`inline-flex px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(alert.status || 'pendente')}`}>
                        {(alert.status || 'pendente').toUpperCase()}
                      </span>
                      <span className="text-xs text-gray-500">
                        {alert.tipoAlerta || 'Sistema'}
                      </span>
                    </div>

                    <p className="text-gray-900 mb-2">{alert.mensagem || 'Sem mensagem'}</p>

                    <div className="flex items-center gap-4 text-sm text-gray-500">
                      {alert.dataCriacao && (
                        <div className="flex items-center gap-1">
                          <Clock className="h-4 w-4" />
                          {format(parseISO(alert.dataCriacao), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                        </div>
                      )}
                      {alert.extintor && (
                        <div>
                          Extintor: {alert.extintor.numeroIdentificacao}
                        </div>
                      )}
                      {alert.usuario && (
                        <div>
                          Por: {alert.usuario.nome}
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center gap-2 ml-4">
                    {(!alert.status || alert.status === 'pendente') && (
                      <button
                        onClick={() => markAsRead(alert.id)}
                        className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                        title="Marcar como lido"
                      >
                        <Eye className="h-5 w-5" />
                      </button>
                    )}
                    {alert.status !== 'resolvido' && (
                      <button
                        onClick={() => markAsResolved(alert.id)}
                        className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors"
                        title="Marcar como resolvido"
                      >
                        <CheckCircle className="h-5 w-5" />
                      </button>
                    )}
                    <button
                      onClick={() => deleteRecord(alert.id)}
                      className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      title="Excluir"
                    >
                      <Trash2 className="h-5 w-5" />
                    </button>
                  </div>
                </div>
              </motion.div>
            ))
          )}
        </motion.div>

        {/* Modal Criar Alerta */}
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-white rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
            >
              <div className="p-6 border-b border-gray-200">
                <h2 className="text-2xl font-bold text-gray-900">Novo Alerta</h2>
              </div>

              <form onSubmit={handleSubmit} className="p-6 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tipo de Alerta *
                  </label>
                  <select
                    value={formData.tipoAlerta}
                    onChange={(e) => setFormData({ ...formData, tipoAlerta: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                    required
                  >
                    <option value="">Selecione o tipo</option>
                    <option value="vencimento">Vencimento</option>
                    <option value="inspecao">Inspeção</option>
                    <option value="manutencao">Manutenção</option>
                    <option value="sistema">Sistema</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Mensagem *
                  </label>
                  <textarea
                    value={formData.mensagem}
                    onChange={(e) => setFormData({ ...formData, mensagem: e.target.value })}
                    rows={4}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Prioridade *
                  </label>
                  <select
                    value={formData.prioridade}
                    onChange={(e) => setFormData({ ...formData, prioridade: e.target.value as any })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                    required
                  >
                    <option value="baixa">Baixa</option>
                    <option value="media">Média</option>
                    <option value="alta">Alta</option>
                    <option value="critica">Crítica</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Extintor (opcional)
                  </label>
                  <select
                    value={formData.extintorId}
                    onChange={(e) => setFormData({ ...formData, extintorId: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                  >
                    <option value="">Selecione um extintor</option>
                    {extintores.map((extintor) => (
                      <option key={extintor.id} value={extintor.id}>
                        {extintor.numeroIdentificacao} - {extintor.localizacao}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex justify-end gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowModal(false)}
                    className="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700"
                  >
                    Criar Alerta
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Alertas
