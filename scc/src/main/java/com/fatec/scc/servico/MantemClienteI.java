package com.fatec.scc.servico;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fatec.scc.model.Cliente;
import com.fatec.scc.model.ClienteRepository;
import com.fatec.scc.model.Endereco;
import com.fatec.scc.ports.MantemCliente;

@Service
public class MantemClienteI implements MantemCliente {
	Logger logger = LogManager.getLogger(MantemClienteI.class);
	@Autowired
	ClienteRepository repository;

	public List<Cliente> consultaTodos() {
		logger.info(">>>>>> servico consultaTodos chamado");
		return repository.findAll();
	}

	@Override
	public Optional<Cliente> consultaPorId(Long id) {
		logger.info(">>>>>> servico consultaPorId chamado");
		return repository.findById(id);
	}

	@Override
	public Cliente save(Cliente cliente) {
		logger.info(">>>>>> servico save chamado ");
		Optional<Cliente> umCliente =consultaPorCpf(cliente.getCpf());
		Endereco endereco = obtemEndereco(cliente.getCep());
		logger.info(">>>>>> save obtem endereco = " + endereco.getLogradouro());
		if (umCliente.isEmpty() & endereco != null) {
			logger.info(">>>>>> controller create - dados validos");
			cliente.obtemDataAtual(new DateTime());
			cliente.setEndereco(endereco.getLogradouro());
			return repository.save(cliente);
		} else {
			return null;

		}
	}
	@Override
	public Cliente altera(Cliente cliente) {
		Optional<Cliente> record = repository.findById(cliente.getId());
		Endereco endereco = obtemEndereco(cliente.getCep());
		logger.info(">>>>>> altera obtem endereco = " + endereco.getLogradouro());
		if (record.isPresent() & endereco != null) {
			record.get().setCpf(cliente.getCpf());
			record.get().setNome(cliente.getNome());
			record.get().setCep(cliente.getCep());
			record.get().setSexo(cliente.getSexo());
			record.get().setDataCadastro(cliente.getDataCadastro());
			record.get().setDataNascimento(cliente.getDataNascimento());
			record.get().setComplemento(cliente.getComplemento());
			record.get().setEndereco(endereco.getLogradouro());
			return repository.save(record.get());
		} else {
			return null;
		}

	}

	@Override
	public void delete(Long id) {
		logger.info(">>>>>> servico delete por id chamado");
		repository.deleteById(id);
	}

	@Override
	public Optional<Cliente> consultaPorCpf(String cpf) {
		logger.info(">>>>>> servico consultaPorCpf chamado");
		return repository.findByCpf(cpf);
	}

	public Endereco obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		logger.info(">>>>>> servico consultaCep - " + cep);
		ResponseEntity<Endereco> resposta = null;
	
		try {
			resposta = template.getForEntity(url, Endereco.class, cep);
			return resposta.getBody();
		} catch (ResourceAccessException e) {
			logger.info(">>>>>> consulta CEP erro nao esperado ");
			return null;
		} catch (HttpClientErrorException e) {
			logger.info(">>>>>> consulta CEP inválido erro HttpClientErrorException =>" + e.getMessage());
			return null;
		}
	}
}
