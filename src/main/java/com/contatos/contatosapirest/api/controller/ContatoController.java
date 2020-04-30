package com.contatos.contatosapirest.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contatos.contatosapirest.domain.models.ContatoModel;
import com.contatos.contatosapirest.domain.repository.ContatoRepository;

@RestController
public class ContatoController {
	ContatoRepository contatoRepository;

	public ContatoController(ContatoRepository contatoRepository) {
		this.contatoRepository = contatoRepository;

	}

	@GetMapping("/contatos")
	public ResponseEntity<List<ContatoModel>> listarContatos() {
		List<ContatoModel> listaContatos = contatoRepository.findAll();
		if (listaContatos.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		for (ContatoModel contato : listaContatos) {
			long id = contato.getId();
			contato.add(linkTo(methodOn(ContatoController.class).buscarContato(id)).withSelfRel());
		}
		return ResponseEntity.ok(listaContatos);
	}

	@GetMapping("/contatos/{id}")
	public ResponseEntity<ContatoModel> buscarContato(@PathVariable(value = "id") long id) {
		Optional<ContatoModel> contatoContainer = contatoRepository.findById(id);
		if (contatoContainer.isPresent()) {
			contatoContainer.get()
					.add(linkTo(methodOn(ContatoController.class).listarContatos()).withRel("Lista de Contatos"));
			return ResponseEntity.ok(contatoContainer.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/contatos")
	public ResponseEntity<ContatoModel> adicionarContato(@RequestBody @Valid ContatoModel contato) {
		return new ResponseEntity<ContatoModel>(contatoRepository.save(contato), HttpStatus.CREATED);
	}

	@DeleteMapping("/contatos/{id}")
	public ResponseEntity<?> deletarContato(@PathVariable(value = "id") long id) {
		Optional<ContatoModel> contatoContainer = contatoRepository.findById(id);
		if (!contatoContainer.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		contatoRepository.delete(contatoContainer.get());
		return ResponseEntity.noContent().build();

	}

	@PutMapping("/contatos/{id}")
	public ResponseEntity<ContatoModel> atualizarContato(@PathVariable(value = "id") long id,
			@RequestBody @Valid ContatoModel contato) {
		Optional<ContatoModel> contatoContainer = contatoRepository.findById(id);
		if (!contatoContainer.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		contato.setId(contatoContainer.get().getId());
		contato = contatoRepository.save(contato);
		return ResponseEntity.ok(contato);
	}

}
