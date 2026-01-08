package com.swole.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swole.platform.model.entity.ExperimentType;
import com.swole.platform.service.ExperimentTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperimentTypeController.class)
@WithMockUser(roles = "ADMIN")
public class ExperimentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExperimentTypeService experimentTypeService;

    private ExperimentType testExperimentType;

    @BeforeEach
    void setUp() {
        testExperimentType = new ExperimentType();
        testExperimentType.setId(1L);
        testExperimentType.setTypeName("Test Type");
    }

    @Test
    public void testGetAllExperimentTypes() throws Exception {
        List<ExperimentType> experimentTypes = Arrays.asList(testExperimentType);
        when(experimentTypeService.getAllExperimentTypes()).thenReturn(experimentTypes);

        mockMvc.perform(get("/api/experiment-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeName").value("Test Type"));

        verify(experimentTypeService, times(1)).getAllExperimentTypes();
    }

    @Test
    public void testGetExperimentTypeById() throws Exception {
        when(experimentTypeService.getExperimentTypeById(1L)).thenReturn(Optional.of(testExperimentType));

        mockMvc.perform(get("/api/experiment-types/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Test Type"));

        verify(experimentTypeService, times(1)).getExperimentTypeById(1L);
    }

    @Test
    public void testGetExperimentTypeByIdNotFound() throws Exception {
        when(experimentTypeService.getExperimentTypeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/experiment-types/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(experimentTypeService, times(1)).getExperimentTypeById(999L);
    }

    @Test
    public void testCreateExperimentType() throws Exception {
        when(experimentTypeService.createExperimentType(any(ExperimentType.class))).thenReturn(testExperimentType);

        mockMvc.perform(post("/api/experiment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExperimentType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Test Type"));

        verify(experimentTypeService, times(1)).createExperimentType(any(ExperimentType.class));
    }

    @Test
    public void testUpdateExperimentType() throws Exception {
        ExperimentType updatedType = new ExperimentType();
        updatedType.setId(1L);
        updatedType.setTypeName("Updated Type");

        when(experimentTypeService.updateExperimentType(eq(1L), any(ExperimentType.class))).thenReturn(updatedType);

        mockMvc.perform(put("/api/experiment-types/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Updated Type"));

        verify(experimentTypeService, times(1)).updateExperimentType(eq(1L), any(ExperimentType.class));
    }

    @Test
    public void testDeleteExperimentType() throws Exception {
        doNothing().when(experimentTypeService).deleteExperimentType(1L);

        mockMvc.perform(delete("/api/experiment-types/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(experimentTypeService, times(1)).deleteExperimentType(1L);
    }

    @Test
    public void testGetExperimentTypeByName() throws Exception {
        when(experimentTypeService.findByTypeName("Test Type")).thenReturn(testExperimentType);

        mockMvc.perform(get("/api/experiment-types/name/{typeName}", "Test Type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Test Type"));

        verify(experimentTypeService, times(1)).findByTypeName("Test Type");
    }
}