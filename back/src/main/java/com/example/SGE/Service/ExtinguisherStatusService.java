package com.example.SGE.Service;

import com.example.SGE.Entity.ExtinguisherEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ExtinguisherStatusService {
    
    /**
     * Calcula o status automático do extintor baseado na data de validade
     * 
     * Lógica:
     * - VENCIDO: Data de validade já passou
     * - PRÓXIMO_AO_VENCIMENTO: Faltam 30 dias ou menos para vencer
     * - CONFORME: Válido e dentro do prazo
     */
    public String calculateStatus(Date validade) {
        if (validade == null) {
            return "conforme";
        }

        LocalDate dataValidade = validade.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        LocalDate today = LocalDate.now();
        LocalDate trinta_dias_depois = today.plusDays(30);

        // Se a data de validade já passou
        if (dataValidade.isBefore(today)) {
            return "vencido";
        }
        
        // Se faltam 30 dias ou menos para vencer
        if (dataValidade.isBefore(trinta_dias_depois) || dataValidade.isEqual(trinta_dias_depois)) {
            return "proximo_ao_vencimento";
        }
        
        // Caso contrário, está conforme
        return "conforme";
    }

    /**
     * Aplica o status automático ao extintor
     */
    public void applyAutoStatus(ExtinguisherEntity extintor) {
        String autoStatus = calculateStatus(extintor.getValidade());
        extintor.setStatus(autoStatus);
    }
}
