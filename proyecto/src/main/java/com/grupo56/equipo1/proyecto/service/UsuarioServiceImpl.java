package com.grupo56.equipo1.proyecto.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo56.equipo1.proyecto.model.RegistroUserDTO;
import com.grupo56.equipo1.proyecto.model.Rol;
import com.grupo56.equipo1.proyecto.model.Usuario;
import com.grupo56.equipo1.proyecto.repository.UserRepository;

@Service
public class UsuarioServiceImpl implements UsuariosService {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

     @Autowired
     private UserRepository userRepository;


     @Override
     public Usuario guardarUser(RegistroUserDTO registroDTO){
        Usuario usuario = new Usuario(registroDTO.getNombre(), registroDTO.getApellido(), 
        registroDTO.getUsername(),registroDTO.getEmail(), passwordEncoder.encode(registroDTO.getClave()),
        Arrays.asList(new Rol("ROLE_USER")));

        return userRepository.save(usuario);
     }


    /* 
    @Override
    public Usuario guardarUser(RegistroUserDTO registroDTO){
        Usuario usuario = new Usuario(registroDTO.getNombre(), registroDTO.getApellido(), 
        registroDTO.getUsername(),registroDTO.getEmail(), passwordEncoder.encode(registroDTO.getClave()));

        return userRepository.save(usuario);
    }
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Usuario usuario = userRepository.findByEmail(username);
        if(usuario == null){
            throw new UsernameNotFoundException("Usuario o contraseña invalidos");
        }

        return new User(usuario.getEmail(), usuario.getClave(), getAuthorities(usuario.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Rol> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
    }
    
}
