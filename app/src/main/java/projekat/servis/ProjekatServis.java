/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.servis;

import projekat.DTO.ProjekatDTO;
import projekat.Mapper.ProjekatMapper;
import java.util.List;
import java.util.stream.Collectors;
import projekat.model.Projekat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.repository.ProjekatRepository;

/**
 *
 * @author Luka
 */
@Service
public class ProjekatServis {

    private final ProjekatRepository projekatRepository;
    private final ProjekatMapper projekatMapper;

    @Autowired
    public ProjekatServis(ProjekatRepository projekatRepository, ProjekatMapper projekatMapper) {
        this.projekatRepository = projekatRepository;
        this.projekatMapper = projekatMapper;
    }

    public List<ProjekatDTO> findAll() {
        return projekatRepository.findAll()
                .stream()
                .map(projekatMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjekatDTO findById(int id) throws Exception {
        return projekatMapper.toDto(projekatRepository.findById(id));
    }

    public ProjekatDTO create(ProjekatDTO dto) {
        Projekat projekat = projekatMapper.toEntity(dto);
        projekatRepository.save(projekat);
        return projekatMapper.toDto(projekat);
    }

    public void deleteById(int id) {
        projekatRepository.deleteById(id);
    }

    public ProjekatDTO update(ProjekatDTO dto) {
        Projekat updated = projekatMapper.toEntity(dto);
        projekatRepository.save(updated);
        return projekatMapper.toDto(updated);
    }
}