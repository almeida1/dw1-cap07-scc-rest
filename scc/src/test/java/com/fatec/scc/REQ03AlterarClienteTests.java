package com.fatec.scc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fatec.scc.model.ApplicationUser;
import com.fatec.scc.model.Cliente;
import com.fatec.scc.ports.ClienteRepository;
import com.fatec.scc.ports.MantemCliente;
import com.google.gson.Gson;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ03AlterarClienteTests {
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	ClienteRepository clienteRepository;
	@Autowired
	MantemCliente mantemCliente;
	@Test
	void ct01_quando_cliente_eh_alterado_com_dados_validos_retorna_alteracao_com_sucesso() {
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso e o cliente que sofre a alteração esta cadastrado
		// **************************************************************************************
		// configura o header com username e senha
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity = new HttpEntity<>(user);
		// tenta autenticar o usuario para obter o token
		ResponseEntity<String> resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST, httpEntity,
				String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());
		// armazena o token no header do post
		HttpHeaders headers = resposta1.getHeaders();
		
		Cliente cliente = new Cliente("Miguel da Silva","20/10/1961","M", "33591113549",  "03694000", "123");
		
		mantemCliente.save(cliente);
		Optional<Cliente> registro = clienteRepository.findByCpf("33591113549");
		Cliente clienteCadastrado = registro.get();
		logger.info(">>>>>> CT01. Cliente cadastrado -> " + clienteCadastrado.toString());
		
		assertTrue(registro.isPresent());
		// **************************************************************************************
		// quando o usuario confirma a alteracao do nome 
		// **************************************************************************************
		cliente.setNome("Novo Nome");
		cliente.setId(clienteCadastrado.getId());
		HttpEntity<Cliente> httpEntity3 = new HttpEntity<>(cliente, headers);
		ResponseEntity<String> resposta = testRestTemplate.exchange("/api/v1/clientes", HttpMethod.PUT, httpEntity3,
				String.class, clienteCadastrado.getId());
		assertEquals("200 OK", resposta.getStatusCode().toString());
		//o resultado retornado como String JSon eh transformado no objeto cliente
		Gson g = new Gson(); 
		Cliente c = g.fromJson(resposta.getBody(), Cliente.class);
		assertEquals("Novo Nome", c.getNome());
		logger.info(">>>>>> CT01. Alterar cliente - Cliente alterado => " + cliente.toString());
	}

}
