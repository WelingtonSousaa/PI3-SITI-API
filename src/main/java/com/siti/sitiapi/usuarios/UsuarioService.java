package com.siti.sitiapi.usuarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {

        boolean emailJaExiste = usuarioRepository.findByEmail(usuario.getEmail()).isPresent();
        if (emailJaExiste) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado no sistema.");
        }

        return usuarioRepository.save(usuario);
    }
}