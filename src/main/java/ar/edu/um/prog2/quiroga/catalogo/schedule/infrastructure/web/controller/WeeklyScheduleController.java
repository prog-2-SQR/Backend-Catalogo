package ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.controller;

import ar.edu.um.prog2.quiroga.catalogo.schedule.application.exception.WeeklyScheduleException;
import ar.edu.um.prog2.quiroga.catalogo.schedule.application.service.WeeklyScheduleService;
import ar.edu.um.prog2.quiroga.catalogo.schedule.domain.model.WeeklySchedule;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.dto.WeeklyScheduleResponse;
import ar.edu.um.prog2.quiroga.catalogo.schedule.infrastructure.web.mapper.WeeklyScheduleDtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class WeeklyScheduleController {

    private final WeeklyScheduleService weeklyScheduleService;
    private final WeeklyScheduleDtoMapper weeklyScheduleDtoMapper;

    /** Lista todos los horarios, o solo los de un profesional si llega {@code professionalId}. */
    @GetMapping
    public ResponseEntity<List<WeeklyScheduleResponse>> getWeeklySchedules(
            @RequestParam(required = false) Long professionalId) {
        List<WeeklySchedule> schedules = professionalId == null
                ? weeklyScheduleService.getAllWeeklySchedules()
                : weeklyScheduleService.findWeeklySchedulesByProfessional(professionalId);
        return ResponseEntity.ok(schedules.stream().map(weeklyScheduleDtoMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeeklyScheduleResponse> getWeeklyScheduleById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(weeklyScheduleDtoMapper.toResponse(weeklyScheduleService.findWeeklyScheduleById(id)));
        } catch (WeeklyScheduleException e) {
            log.warn("Horario no encontrado: id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
