package com.biblioteca.service;

import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro cadastrar(Livro livro) {
        livro.setId(null);
        return repository.save(livro);
    }

    public List<Livro> listar() {
        return repository.findAll();
    }

    public Livro consultar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    public Livro atualizar(Long id, Livro dados) {
        Livro livro = consultar(id);
        livro.setTitulo(dados.getTitulo());
        livro.setAutor(dados.getAutor());
        livro.setGenero(dados.getGenero());
        livro.setAnoPublicacao(dados.getAnoPublicacao());
        livro.setStatusLeitura(dados.getStatusLeitura());
        return repository.save(livro);
    }

    public void excluir(Long id) {
        Livro livro = consultar(id);
        repository.delete(livro);
    }
}
