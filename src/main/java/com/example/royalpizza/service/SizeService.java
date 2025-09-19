package com.example.royalpizza.service;

import com.example.royalpizza.entity.Size;
import com.example.royalpizza.repository.SizeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }
    public Size getSizeByName(String name) {
        List<Size> listeSize = sizeRepository.findByNameSize(name);
        return listeSize.isEmpty() ? null : listeSize.get(0);
    }
}
