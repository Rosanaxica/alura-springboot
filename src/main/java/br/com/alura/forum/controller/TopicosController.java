package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.modelo.TopicoForm;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("/topicos")
@RestController
public class TopicosController {

@Autowired
private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<TopicoDto> lista(String nomeCurso){
        if(nomeCurso==null){
            List<Topico> topicos= topicoRepository.findAll();
            return TopicoDto.converter(topicos);
        }else{
            List<Topico> topicos= topicoRepository.findByCursoNome(nomeCurso);
            return TopicoDto.converter(topicos);
        }


    }

    @PostMapping
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriComponentsBuilder){
        Topico topico = form.converter(cursoRepository);
        topicoRepository.save(topico);
        URI uri= uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
                return ResponseEntity.created(uri).body(new TopicoDto((topico)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detahar(@PathVariable Long id) {
        Optional<Topico> topico =topicoRepository.findById(id);
        if(topico.isPresent()){
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public  ResponseEntity<TopicoDto>  detahar(@PathVariable Long id , @RequestBody @Valid AtualizacaoTopicoForm form) {
        Optional<Topico> topico = topicoRepository.findById(id);
        if(topico.isPresent()){
        form.atualizar(id,topicoRepository);
            return ResponseEntity.ok(new TopicoDto(topico.get()));
        }

        return  ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        Optional<Topico> topico =topicoRepository.findById(id);
        if(topico.isPresent()){
            topicoRepository.deleteById(id);

            return  ResponseEntity.noContent().build();
        }

        return  ResponseEntity.notFound().build();
    }

}


