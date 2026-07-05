/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.servis;

import projekat.DTO.TimDTO;
import projekat.Mapper.TimMapper;
import java.util.List;
import java.util.stream.Collectors;
import projekat.model.Tim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.repository.TimRepository;

/**
 *
 * @author Luka
 */
@Service
public class TimServis {

    private final TimRepository timRepository;
    private final TimMapper timMapper;

    @Autowired
    public TimServis(TimRepository timRepository, TimMapper timMapper) {
        this.timRepository = timRepository;
        this.timMapper = timMapper;
    }

    public List<TimDTO> findAll() {
        return timRepository.findAll()
                .stream()
                .map(timMapper::toDto)
                .collect(Collectors.toList());
    }

    public TimDTO findById(int id) throws Exception {
        return timMapper.toDto(timRepository.findById(id));
    }

    public TimDTO create(TimDTO dto) {
        Tim tim = timMapper.toEntity(dto);
        timRepository.save(tim);
        return timMapper.toDto(tim);
    }

    public void deleteById(int id) {
        timRepository.deleteById(id);
    }

    public TimDTO update(TimDTO dto) {
        Tim updated = timMapper.toEntity(dto);
        timRepository.save(updated);
        return timMapper.toDto(updated);
    }
}