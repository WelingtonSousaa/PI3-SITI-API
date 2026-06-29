package com.siti.sitiapi.scheduler;

import com.siti.sitiapi.service.EmailService;
import com.siti.sitiapi.service.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VotingScheduler {

    private final JdbcTemplate jdbc;
    private final EmailService emailService;
    private final RouteOptimizationService routeOptimizationService;

    // Roda a cada 5 minutos para checar se deve fechar a janela
    @Scheduled(fixedRate = 300000)
    public void checkVotingWindow() {
        List<Map<String, Object>> settings = jdbc.queryForList("SELECT * FROM settings LIMIT 1");
        if (settings.isEmpty()) return;

        String closeTimeStr = (String) settings.get(0).get("close_time");
        if (closeTimeStr == null) return;

        LocalTime closeTime = LocalTime.parse(closeTimeStr);
        LocalTime now = LocalTime.now();

        // RF024: Lembretes 30 min antes
        if (now.isAfter(closeTime.minusMinutes(35)) && now.isBefore(closeTime.minusMinutes(25))) {
            sendReminders();
        }

        // RF010: Fechamento da janela e disparo de lógicas
        if (now.isAfter(closeTime) && now.isBefore(closeTime.plusMinutes(6))) {
            System.out.println("Fechando janela de votação e otimizando rotas...");
            routeOptimizationService.optimizeStopsForToday();
            routeOptimizationService.analyzeCapacityAndDemand();
        }
    }

    private void sendReminders() {
        // Pega todos passageiros que AINDA NÃO VOTARAM hoje
        List<String> emails = jdbc.queryForList(
                "SELECT u.email FROM users u " +
                "JOIN passengers p ON u.id = p.id " +
                "WHERE u.id NOT IN (SELECT id_passenger FROM votes WHERE voted_date = CURRENT_DATE())", 
                String.class);

        for (String email : emails) {
            emailService.sendSimpleMessage(email, 
                    "Lembrete: Votação SITI encerra em breve!", 
                    "Não esqueça de registrar seu interesse de viagem para hoje no app SITI.");
        }
    }
}
