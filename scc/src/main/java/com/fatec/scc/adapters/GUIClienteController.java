package com.fatec.scc.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.scc.model.Cliente;

import com.fatec.scc.ports.MantemCliente;

@RestController // controller deve retornar valores escritos diretamente no body da resposta
@RequestMapping("/api/v1/clientes")
public class GUIClienteController {
	// https://stackabuse.com/how-to-return-http-status-codes-in-a-spring-boot-application/
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	MantemCliente servico;

	@PostMapping
	public ResponseEntity<?> cadastrar(@Valid @RequestBody Cliente cliente, BindingResult result) {
		logger.info(">>>>>> 1. controller cadastrar - post iniciado");
		ResponseEntity<?> response = null;
		if (result.hasErrors()) {
			logger.info(">>>>>> controller cadastrar - dados inválidos => " + cliente.toString());
			response = ResponseEntity.badRequest().body("Dados inválidos.");
		} else {
			Cliente umCliente = servico.save(cliente);
			if (umCliente != null) {
				logger.info(">>>>>> controller create - dados validos");
				response = ResponseEntity.status(HttpStatus.CREATED).body(umCliente);
			} else {
				response = ResponseEntity.badRequest().body("Cliente já cadastrado ou CEP inválido");
				logger.info(">>>>>> controller create - cadastro realizado com sucesso");
			}
		}
		return response;
	}

	@GetMapping
	public ResponseEntity<List<Cliente>> consultaTodos() {
		logger.info(">>>>>> 1. controller consulta todos chamado");
		List<Cliente> listaDeClientes = new ArrayList<Cliente>();
		servico.consultaTodos().forEach(listaDeClientes::add);
		if (listaDeClientes.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(listaDeClientes, HttpStatus.OK);
	}

	@GetMapping("/{cpf}")
	public ResponseEntity<Cliente> findByCpf(@PathVariable String cpf) {
		logger.info(">>>>>> 1. controller chamou servico consulta por cpf => " + cpf);
		ResponseEntity<Cliente> response = null;
		Optional<Cliente> optCliente  = servico.consultaPorCpf(cpf);
		
		if (optCliente.isPresent()) {
			response = ResponseEntity.status(HttpStatus.OK).body(optCliente.get());
		} else {
			response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return response;
	}

	@GetMapping("/id/{id}") // pagina 144 do spring in action
	public ResponseEntity<Cliente> findById(@PathVariable String id) {
		logger.info(">>>>>> 1. controller chamou servico consulta por id => " + id);
		Long ident = Long.parseLong(id);
		Optional<Cliente> cliente = servico.consultaPorId(ident);
		if (cliente.isPresent()) {
			return new ResponseEntity<>(cliente.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/id/{id}")
	public ResponseEntity<Cliente> update(@PathVariable("id") long id, @RequestBody @Valid Cliente clienteModificado) {// throws
		// URISyntaxException
		// {
		logger.info(">>>>>> 1. controller alterar - put iniciado id => " + clienteModificado.getId());
		Cliente cliente = servico.altera(clienteModificado);
		if (cliente != null) {
			logger.info(">>>>>> controller update - cliente cadastrado para put update");
			return new ResponseEntity<>(servico.altera(cliente), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/id/{id}")
	public ResponseEntity<String> remover(@PathVariable String id) {
		Long ident = Long.parseLong(id);
		Optional<Cliente> cliente = servico.consultaPorId(ident);

		if (cliente.isPresent()) {
			servico.delete(ident);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		logger.info(">>>>>> exception handler" + ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}
}