"use client";

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Plus,
  Search,
  Filter,
  Edit,
  Trash2,
  MapPin,
  Calendar,
  AlertTriangle,
  FireExtinguisher
} from "lucide-react";
import { useCRUD } from "../../hooks/useCRUD";
import { Extinguisher, ExtinguisherStatus } from "../../types";
import { format, isValid, parseISO } from "date-fns";
import { ptBR } from "date-fns/locale";
import MainHeader from "@/components/MainHeader";

const ExtintoresPage: React.FC = () => {
  // Função para formatar data de forma segura
  const formatSafeDate = (dateValue: any): string => {
    if (!dateValue) return "Data inválida";
    
    try {
      let date: Date;
      // Se já é uma Date
      if (dateValue instanceof Date) {
        date = dateValue;
      } 
      // Se é string ISO
      else if (typeof dateValue === 'string') {
        date = parseISO(dateValue);
      }
      // Se é timestamp
      else if (typeof dateValue === 'number') {
        date = new Date(dateValue);
      }
      // Fallback
      else {
        date = new Date(dateValue);
      }
      
      if (!isValid(date)) {
        return "Data inválida";
      }
      
      return format(date, "dd/MM/yyyy", { locale: ptBR });
    } catch (error) {
      console.error("Erro ao formatar data:", error, "Valor:", dateValue);
      return "Data inválida";
    }
  };

  // Função para formatar data para input type="date"
  const formatDateForInput = (dateValue: any): string => {
    if (!dateValue) return '';
    
    try {
      let date: Date;
      
      if (dateValue instanceof Date) {
        date = dateValue;
      } 
      else if (typeof dateValue === 'string') {
        date = parseISO(dateValue);
      }
      else if (typeof dateValue === 'number') {
        date = new Date(dateValue);
      }
      else {
        date = new Date(dateValue);
      }
      
      if (!isValid(date)) {
        return '';
      }
      
      return date.toISOString().split('T')[0];
    } catch (error) {
      console.error("Erro ao formatar data para input:", error, "Valor:", dateValue);
      return '';
    }
  };

  // Utilitários para renderização dos cards
  const getStatusColor = (status?: string) => {
    switch (status) {
      case "conforme":
        return "bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium";
      case "proximo_ao_vencimento":
        return "bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-medium";
      case "nao_conforme":
        return "bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-medium";
      case "vencido":
        return "bg-orange-100 text-orange-800 px-2 py-1 rounded-full text-xs font-medium";
      case "manutencao":
        return "bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-medium";
      default:
        return "bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs font-medium";
    }
  };

  const getStatusText = (status?: string) => {
    switch (status) {
      case "conforme":
        return "Conforme";
      case "proximo_ao_vencimento":
        return "Próximo ao Vencimento";
      case "nao_conforme":
        return "Não Conforme";
      case "vencido":
        return "Vencido";
      case "manutencao":
        return "Em Manutenção";
      default:
        return status;
    }
  };

  const getUnidadeNome = (unidadeId: string | number) => {
    return unidadeId ? unidadeId.toString() : "Unidade não informada";
  };

  const getTipoAgenteText = (agentType: string) => {
    switch (agentType) {
      case "po_abc":
        return "Pó ABC";
      case "co2":
        return "CO2";
      case "agua":
        return "Água";
      case "espuma":
        return "Espuma";
      case "po_quimico":
        return "Pó Químico";
      default:
        return agentType;
    }
  };

  const {
    data: rawExtintores,
    loading,
    createRecord,
    updateRecord,
    deleteRecord,
  } = useCRUD<Extinguisher>("extinguisher");

  const {
    data: unidades,
  } = useCRUD("unidade");
  
  // Função para sanitizar dados de extintores
  const sanitizeExtinguisher = (extintor: any): Extinguisher => {
    return {
      ...extintor,
      validade: extintor.validade || null,
      dataFabricacao: extintor.dataFabricacao || null,
    };
  };

  // Sanitizar dados antes de usar
  const extintores = React.useMemo(() => {
    if (!rawExtintores || !Array.isArray(rawExtintores)) {
      return [];
    }
    return rawExtintores.map(sanitizeExtinguisher);
  }, [rawExtintores]);

  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    setHasMounted(true);
  }, []);
  const [showForm, setShowForm] = useState(false);
  const [editingExtintor, setEditingExtintor] = useState<Extinguisher | null>(null);
  const [generatedNumber, setGeneratedNumber] = useState<string>("");
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [tipoFilter, setTipoFilter] = useState("");

  // Função para gerar número de identificação automático
  const generateExtinguisherNumber = () => {
    const nextNumber = (rawExtintores?.length || 0) + 1;
    const paddedNumber = String(nextNumber).padStart(5, '0');
    return `EXT-${paddedNumber}`;
  };

  // Abrir formulário com número gerado
  const handleOpenForm = () => {
    setEditingExtintor(null);
    setGeneratedNumber(generateExtinguisherNumber());
    setShowForm(true);
  };

  // Abrir formulário para editar
  const handleEditForm = (extintor: Extinguisher) => {
    setEditingExtintor(extintor);
    setGeneratedNumber("");
    setShowForm(true);
  };

  // Filtrar extintores (definido de forma segura para evitar campos undefined)
  const safeExtintores = Array.isArray(extintores) ? extintores : [];

  const filteredExtintores = safeExtintores.filter((extintor: Extinguisher) => {
    const numero = (extintor.numeroIdentificacao ?? "").toString().toLowerCase();
    const local = (extintor.localizacao ?? "").toString().toLowerCase();
    const term = searchTerm.toLowerCase();

    const matchesSearch = term === "" || numero.includes(term) || local.includes(term);
    const matchesStatus = !statusFilter || extintor.status === statusFilter;
    const matchesTipo = !tipoFilter || extintor.tipoAgente === tipoFilter;
    return matchesSearch && matchesStatus && matchesTipo;
  });

  // Função para enviar o formulário
  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);

    console.log('📋 Form Data - Raw:', {
      numeroIdentificacao: formData.get("numeroIdentificacao"),
      unidadeId: formData.get("unidadeId"),
      dataFabricacao: formData.get("dataFabricacao"),
      dataValidade: formData.get("dataValidade"),
    });

    // Mapeando dados do formulário para a estrutura do backend
    const extintorData: Omit<Extinguisher, 'id'> & { status?: ExtinguisherStatus } = {
      numeroIdentificacao: formData.get("numeroIdentificacao") as string,
      localizacao: formData.get("localizacao") as string,
      validade: formData.get("dataValidade") as string,
      tipoAgente: formData.get("tipoAgente") as string,
      classeIncendio: formData.get("classeIncendio") as string,
      capacidade: formData.get("capacidade") as string,
      dataFabricacao: formData.get("dataFabricacao") as string,
      fabricante: formData.get("fabricante") as string,
      observacoes: (formData.get("observacoes") as string) || "",
      unidadeId: Number(formData.get("unidadeId") as string),
    };

    const statusSelecionado = formData.get("status") as string | null;
    if (statusSelecionado) {
      extintorData.status = statusSelecionado as ExtinguisherStatus;
    }

    console.log('📋 Extintor Data - Preparado para envio:', formData);

    try {
      if (editingExtintor && editingExtintor.id) {
        console.log('✏️ Modo: EDIÇÃO - ID:', editingExtintor.id);
        await updateRecord(editingExtintor.id, extintorData);
        console.log("Data", extintorData)
      } else {
        console.log('📝 Modo: CRIAÇÃO');
        await createRecord(extintorData);
      }
      setShowForm(false);
      setEditingExtintor(null);
    } catch (error) {
      console.error('❌ Erro ao salvar extintor:', error);
    }
  };

  const handleDelete = async (extintor: Extinguisher) => {
    if (
      confirm(
        `Tem certeza que deseja excluir o extintor ${extintor.numeroIdentificacao}?`
      )
    ) {
      await deleteRecord(extintor.id!);
    }
  };

  if (!hasMounted || loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex justify-center items-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      {/* Header aprimorado */}
      <div className="max-w-7xl mx-auto">
        <MainHeader
          icon={<FireExtinguisher className="w-8 h-8 text-red-600" />}
          textHeader="Extintores"
          subtitle="Cadastro e controle de equipamentos"
          showButton={true}
          buttonText="Novo Extintor"
          buttonIcon={<Plus className="w-5 h-5" />}
          onButtonClick={handleOpenForm}
        />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filtros */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-8"
        >
          <div className="flex items-center space-x-4 mb-4">
            <Filter className="w-5 h-5 text-gray-500" />
            <h3 className="text-lg font-medium text-gray-900">Filtros</h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="relative">
              <Search className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
              <input
                type="text"
                placeholder="Buscar por número ou localização..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="input-field pl-10"
              />
            </div>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="input-field"
            >
              <option value="">Todos os status</option>
              <option value="conforme">Conforme</option>
              <option value="proximo_ao_vencimento">Próximo ao Vencimento</option>
              <option value="nao_conforme">Não Conforme</option>
              <option value="vencido">Vencido</option>
              <option value="manutencao">Em Manutenção</option>
            </select>
            <select
              value={tipoFilter}
              onChange={(e) => setTipoFilter(e.target.value)}
              className="input-field"
            >
              <option value="">Todos os tipos</option>
              <option value="po_abc">Pó ABC</option>
              <option value="co2">CO2</option>
              <option value="agua">Água</option>
              <option value="espuma">Espuma</option>
              <option value="po_quimico">Pó Químico</option>
            </select>
            <div className="text-sm text-gray-500 flex items-center">
              <span className="font-medium">{filteredExtintores.length}</span>
              <span className="ml-1">extintor(es) encontrado(s)</span>
            </div>
          </div>
        </motion.div>

        {/* Lista de extintores */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6"
        >
          {filteredExtintores && Array.isArray(filteredExtintores) ? 
            filteredExtintores.map((extintor: Extinguisher, index: number) => {
              // Verificação adicional para cada extintor
              if (!extintor || typeof extintor !== 'object') {
                console.warn("Extintor inválido encontrado:", extintor);
                return null;
              }

              return (
                <motion.div
                  key={extintor.id || `extintor-${index}`}
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.05 * index }}
              className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-lg transition-all duration-200"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <FireExtinguisher className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">
                      {extintor.numeroIdentificacao}
                    </h3>
                    <span className={getStatusColor(extintor.status)}>
                      {getStatusText(extintor.status)}
                    </span>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleEditForm(extintor)}
                    className="p-2 text-gray-400 hover:text-blue-600 transition-colors"
                  >
                    <Edit className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(extintor)}
                    className="p-2 text-gray-400 hover:text-red-600 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
              <div className="space-y-3">
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <MapPin className="w-4 h-4" />
                  <span>{extintor.localizacao}</span>
                </div>
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <span className="font-medium">Unidade:</span>
                  <span>{getUnidadeNome(extintor.unidadeId)}</span>
                </div>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="text-gray-500">Tipo:</span>
                    <p className="font-medium">
                      {getTipoAgenteText(extintor.tipoAgente)}
                    </p>
                  </div>
                  <div>
                    <span className="text-gray-500">Classe:</span>
                    <p className="font-medium">{extintor.classeIncendio}</p>
                  </div>
                  <div>
                    <span className="text-gray-500">Capacidade:</span>
                    <p className="font-medium">{extintor.capacidade}</p>
                  </div>
                  <div>
                    <span className="text-gray-500">Fabricante:</span>
                    <p className="font-medium">{extintor.fabricante}</p>
                  </div>
                </div>
                <div className="flex items-center justify-between pt-3 border-t border-gray-200">
                  <div className="flex items-center space-x-2 text-xs text-gray-500">
                    <Calendar className="w-4 h-4" />
                    <span>
                      Validade: {formatSafeDate(extintor.validade)}
                    </span>
                  </div>
                  {extintor.status === "vencido" && (
                    <div className="flex items-center space-x-1 text-orange-600">
                      <AlertTriangle className="w-4 h-4" />
                      <span className="text-xs font-medium">Vencido</span>
                    </div>
                  )}
                </div>
              </div>
            </motion.div>
              );
            }).filter(Boolean) : 
            <div className="text-center text-gray-500">Carregando extintores...</div>
          }
        </motion.div>

        {filteredExtintores.length === 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-12"
          >
            <FireExtinguisher className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">
              Nenhum extintor encontrado
            </h3>
            <p className="mt-1 text-sm text-gray-500">
              {searchTerm || statusFilter || tipoFilter
                ? "Tente ajustar os filtros de busca."
                : "Comece cadastrando seu primeiro extintor."}
            </p>
          </motion.div>
        )}

        {/* Modal de formulário */}
        {showForm && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-white rounded-xl shadow-xl w-full max-w-4xl max-h-[90vh] overflow-y-auto"
            >
              <div className="p-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-8">
                  {editingExtintor ? "Editar Extintor" : "Novo Extintor"}
                </h2>
                
                <form onSubmit={handleSubmit} className="space-y-6">
                  {/* Primeira linha - Número e Unidade */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Número de Identificação *
                      </label>
                      <input
                        name="numeroIdentificacao"
                        type="text"
                        value={editingExtintor ? editingExtintor.numeroIdentificacao : generatedNumber}
                        onChange={(e) => !editingExtintor && setGeneratedNumber(e.target.value)}
                        required
                        className={`input-field ${editingExtintor ? '' : 'bg-gray-100'}`}
                        placeholder="EXT-00147"

                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Unidade *
                      </label>
                      <select
                        name="unidadeId"
                        defaultValue={editingExtintor?.unidadeId || ''}
                        required
                        className="input-field"
                      >
                        <option value="">Selecione uma unidade</option>
                        {unidades && Array.isArray(unidades) && unidades.map((unidade: any) => (
                          <option key={unidade.id} value={unidade.id}>
                            {unidade.nome}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  {/* Localização - Linha inteira */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Localização *
                    </label>
                    <input
                      name="localizacao"
                      type="text"
                      defaultValue={editingExtintor?.localizacao || ''}
                      required
                      className="input-field"
                      placeholder="Ex: Corredor principal - Próximo à recepção"
                    />
                  </div>

                  {/* Tipo de Agente e Classe de Incêndio */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Tipo de Agente *
                      </label>
                      <select
                        name="tipoAgente"
                        defaultValue={editingExtintor?.tipoAgente || ''}
                        required
                        className="input-field"
                      >
                        <option value="">Selecione o tipo</option>
                        <option value="po_abc">Pó ABC</option>
                        <option value="co2">CO2</option>
                        <option value="agua">Água</option>
                        <option value="espuma">Espuma</option>
                        <option value="po_quimico">Pó Químico</option>
                      </select>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Classe de Incêndio *
                      </label>
                      <select
                        name="classeIncendio"
                        defaultValue={editingExtintor?.classeIncendio || ''}
                        required
                        className="input-field"
                      >
                        <option value="">Selecione a classe</option>
                        <option value="A">Classe A</option>
                        <option value="B">Classe B</option>
                        <option value="C">Classe C</option>
                        <option value="AB">Classe AB</option>
                        <option value="BC">Classe BC</option>
                        <option value="ABC">Classe ABC</option>
                      </select>
                    </div>
                  </div>

                  {/* Capacidade e Fabricante */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Capacidade *
                      </label>
                      <div className="flex gap-2">
                        <input
                          name="capacidade"
                          type="number"
                          defaultValue={editingExtintor?.capacidade?.toString().match(/\d+/)?.[0] || ''}
                          required
                          className="input-field flex-1"
                          placeholder="Ex: 4, 6, 12"
                        />
                        <select className="w-20 input-field">
                          <option>kg</option>
                          <option>L</option>
                        </select>
                      </div>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Fabricante
                      </label>
                      <input
                        name="fabricante"
                        type="text"
                        defaultValue={editingExtintor?.fabricante || ''}
                        className="input-field"
                        placeholder="Nome do fabricante"
                      />
                    </div>
                  </div>

                  {/* Datas */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Data de Fabricação *
                      </label>
                      <input
                        name="dataFabricacao"
                        type="date"
                        defaultValue={formatDateForInput(editingExtintor?.dataFabricacao)}
                        required
                        className="input-field"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Data de Validade *
                      </label>
                      <input
                        name="dataValidade"
                        type="date"
                        defaultValue={formatDateForInput(editingExtintor?.validade)}
                        required
                        className="input-field"
                      />
                    </div>
                  </div>

                  {/* Status Automático / Manual */}
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 space-y-4">
                    <div className="flex gap-3">
                      <AlertTriangle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                      <div>
                        <h4 className="font-semibold text-blue-900 mb-2">Status Automático</h4>
                        <p className="text-sm text-blue-800 mb-2">Por padrão o status é calculado com base na validade:</p>
                        <ul className="text-sm text-blue-800 space-y-1 ml-4 list-disc">
                          <li><span className="font-medium text-green-700">Conforme</span>: Válido e dentro do prazo</li>
                          <li><span className="font-medium text-yellow-700">Próximo ao Vencimento</span>: Faltam 30 dias ou menos</li>
                          <li><span className="font-medium text-red-700">Vencido</span>: Data de validade já passou</li>
                        </ul>
                      </div>
                    </div>

                    {editingExtintor && (
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-blue-900 mb-2">
                            Status (apenas edição)
                          </label>
                          <select
                            name="status"
                            defaultValue={editingExtintor.status || ""}
                            className="input-field"
                          >
                            <option value="">Automático pela validade</option>
                            <option value="conforme">Conforme</option>
                            <option value="proximo_ao_vencimento">Próximo ao Vencimento</option>
                            <option value="vencido">Vencido</option>
                            <option value="nao_conforme">Não Conforme</option>
                            <option value="manutencao">Em Manutenção</option>
                          </select>
                          <p className="text-xs text-blue-800 mt-1">
                            Selecione apenas quando precisar forçar um status específico. Deixe em branco para seguir o cálculo automático.
                          </p>
                        </div>
                        <div className="text-sm text-blue-900">
                          <p className="font-semibold mb-1">Dica</p>
                          <p>Ao salvar uma inspeção o sistema também ajusta o status automaticamente conforme o resultado.</p>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* Observações */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Observações
                    </label>
                    <textarea
                      name="observacoes"
                      rows={3}
                      defaultValue={editingExtintor?.observacoes || ''}
                      className="input-field resize-none"
                      placeholder="Observações gerais sobre o extintor..."
                    />
                  </div>

                  {/* Botões de ação */}
                  <div className="flex justify-end gap-3 pt-6 border-t border-gray-200">
                    <button
                      type="button"
                      onClick={() => { setShowForm(false); setEditingExtintor(null) }}
                      className="px-6 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg font-medium transition-colors"
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="px-6 py-2 text-white bg-red-600 hover:bg-red-700 rounded-lg font-medium transition-colors"
                    >
                      {editingExtintor ? 'Atualizar' : 'Confirmar'}
                    </button>
                  </div>
                </form>
              </div>
            </motion.div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ExtintoresPage;
