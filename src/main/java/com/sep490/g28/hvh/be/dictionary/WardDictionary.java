package com.sep490.g28.hvh.be.dictionary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WardDictionary {

    private Set<String> wards;

    @PostConstruct
    public void load() throws IOException {
        //load data to memory after app started

        ObjectMapper mapper = new ObjectMapper();

        InputStream is = new ClassPathResource("dictionary/wards.json")
                .getInputStream();

        List<Map<String, Object>> list =
                mapper.readValue(is, new TypeReference<>() {});

        wards = list.stream()
                .map(m -> (String) m.get("ten_moi"))
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean contains(String ward) {
        if (ward == null) return false;
        return wards.contains(ward.trim());
    }

    public Set<String> getAll() {
        return wards;
    }
}
