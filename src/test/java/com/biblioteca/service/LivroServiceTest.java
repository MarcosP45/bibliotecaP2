package com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Livro;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LivroServiceTest {

    @Autowired
    private LivroService service;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    private Livro novoLivro() {
        return new Livro("Dom Casmurro", "Machado de Assis", "Romance", 1899, StatusLeitura.NAO_LIDO);
    }

    @Test
    void deveCadastrarLivro() {
        Livro salvo = service.cadastrar(novoLivro());

        assertNotNull(salvo.getId());
        assertEquals("Dom Casmurro", salvo.getTitulo());
    }

    @Test
    void deveListarLivros() {
        service.cadastrar(novoLivro());
        service.cadastrar(new Livro("1984", "George Orwell", "Distopia", 1949, StatusLeitura.LIDO));

        assertEquals(2, service.listar().size());
    }

    @Test
    void deveConsultarLivroPorId() {
        Livro salvo = service.cadastrar(novoLivro());

        Livro encontrado = service.consultar(salvo.getId());

        assertEquals(salvo.getId(), encontrado.getId());
        assertEquals("Machado de Assis", encontrado.getAutor());
    }

    @Test
    void deveLancarExcecaoAoConsultarIdInexistente() {
        assertThrows(LivroNaoEncontradoException.class, () -> service.consultar(999L));
    }

    @Test
    void deveAtualizarLivro() {
        Livro salvo = service.cadastrar(novoLivro());
        Livro dados = new Livro("Dom Casmurro", "Machado de Assis", "Clássico", 1899, StatusLeitura.LIDO);

        Livro atualizado = service.atualizar(salvo.getId(), dados);

        assertEquals("Clássico", atualizado.getGenero());
        assertEquals(StatusLeitura.LIDO, atualizado.getStatusLeitura());
    }

    @Test
    void deveLancarExcecaoAoAtualizarIdInexistente() {
        assertThrows(LivroNaoEncontradoException.class, () -> service.atualizar(999L, novoLivro()));
    }

    @Test
    void deveExcluirLivro() {
        Livro salvo = service.cadastrar(novoLivro());

        service.excluir(salvo.getId());

        assertTrue(service.listar().isEmpty());
    }

    @Test
    void deveLancarExcecaoAoExcluirIdInexistente() {
        assertThrows(LivroNaoEncontradoException.class, () -> service.excluir(999L));
    }
}
