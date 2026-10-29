package com.biblioteca.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.biblioteca.model.Livro;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    private Livro salvarLivro() {
        return repository.save(new Livro("O Hobbit", "J. R. R. Tolkien", "Fantasia", 1937, StatusLeitura.LENDO));
    }

    @Test
    void deveCadastrarLivro() throws Exception {
        Livro livro = new Livro("Duna", "Frank Herbert", "Ficção científica", 1965, StatusLeitura.NAO_LIDO);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo", is("Duna")));
    }

    @Test
    void naoDeveCadastrarLivroInvalido() throws Exception {
        Livro livro = new Livro("", "", "", null, null);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(livro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.autor").exists());
    }

    @Test
    void deveListarLivros() throws Exception {
        salvarLivro();

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveConsultarLivroPorId() throws Exception {
        Livro salvo = salvarLivro();

        mockMvc.perform(get("/livros/" + salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("O Hobbit")));
    }

    @Test
    void deveRetornar404AoConsultarIdInexistente() throws Exception {
        mockMvc.perform(get("/livros/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        Livro salvo = salvarLivro();
        Livro dados = new Livro("O Hobbit", "J. R. R. Tolkien", "Fantasia", 1937, StatusLeitura.LIDO);

        mockMvc.perform(put("/livros/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusLeitura", is("LIDO")));
    }

    @Test
    void deveExcluirLivro() throws Exception {
        Livro salvo = salvarLivro();

        mockMvc.perform(delete("/livros/" + salvo.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/livros/" + salvo.getId()))
                .andExpect(status().isNotFound());
    }
}
