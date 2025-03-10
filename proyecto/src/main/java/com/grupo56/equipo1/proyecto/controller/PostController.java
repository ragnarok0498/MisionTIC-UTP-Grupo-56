package com.grupo56.equipo1.proyecto.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.grupo56.equipo1.proyecto.model.Comment;
import com.grupo56.equipo1.proyecto.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo56.equipo1.proyecto.model.Post;
import com.grupo56.equipo1.proyecto.service.PostService;

@Controller
public class PostController {

    
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/posts")
    public String listarPosts(Model modelo){
        modelo.addAttribute("posts", postService.listarPosts());
        modelo.addAttribute("postsi", postService.listarPostsInactive());
        return "forms/control_entradas";
    }



    @GetMapping("/posts/newpost")
    public String crearPost(Model modelo){

        Post post = new Post();
        modelo.addAttribute("post", post);
        return "forms/publicar_entrada";
    }


    //Creamos nuevo post
    @PostMapping("/posts/newpost")
    public String guardarPost(@RequestParam(name = "foto", required = false) MultipartFile foto, @ModelAttribute("post") Post post, RedirectAttributes flash){
       
        if(!foto.isEmpty()){
            //String ruta = "D:/Programacion/Proyecto_Ciclo_3/MisionTIC-UTP-Grupo-56/proyecto/src/main/resources/imagenes/";
            String ruta = "C:/Users/willv/Documents/1. Proyecto/MisionTIC-UTP-Grupo-56/proyecto/src/main/resources/imagenes/";
            try {
                Path rutaAbsoluta = Paths.get(ruta +"//"+ foto.getOriginalFilename());
                Files.write(rutaAbsoluta, foto.getBytes());
                post.setImagen(foto.getOriginalFilename());
               

            } catch (Exception e) {
                
            }
        }

        postService.guardarPost(post);
        flash.addFlashAttribute("success", "Publicación guardada con exito");
        return "redirect:/posts";
    } 


    //Editamos post
    @GetMapping("/posts/edit/{id}")
    public String mostrarFormularioEdit(@PathVariable Long id, Model model){
        
        model.addAttribute("post", postService.obtenerPostId(id));

        return "forms/editar_entrada";

    }

    @PostMapping("/posts/{id}")
    public String actualizarPost(@PathVariable Long id, @ModelAttribute("post")Post post, Model model){
    
        Post postExiste = postService.obtenerPostId(id);
        postExiste.setId_publicacion(id);
        postExiste.setTitulo(post.getTitulo());
        postExiste.setDescripcion(post.getDescripcion());
        postExiste.setResumen(post.getResumen());
        postExiste.setCategoria(post.getCategoria());

        postService.actualizarPost(postExiste);
        return "redirect:/posts";
    }

    //Eliminamos post
    
    @GetMapping("/posts/{id}")
    public String eliminarPost(@PathVariable Long id ){
        postService.eliminaPost(id);

        return "redirect:/posts";
    }

    @GetMapping("/")
    public String listarPostsIndex(Model modelo){
        modelo.addAttribute("posts", postService.listarPosts());
        return "index";
    }

    @GetMapping("/entrada/{id}")
    public String listarPostsEntrada(@PathVariable Long id, Model model){
        model.addAttribute("post", postService.obtenerPostId(id));
        model.addAttribute("comments", commentService.listarAllComments());


        return "forms/entrada";
    }

    //Activamos publicacion
    @GetMapping("/posts/active/{id}")
    public String actualizarEstadoPost(@PathVariable Long id, @ModelAttribute("post")Post post, Model model){
    
        Post postExiste = postService.obtenerPostId(id);
        postExiste.setEstado("1");

        postService.actualizarEstadoPost(postExiste);
        return "redirect:/posts";
    }


    //Desactivamos publicacion
    @GetMapping("/posts/inactive/{id}")
    public String actualizarIEstadoPost(@PathVariable Long id, @ModelAttribute("post")Post post, Model model){
    
        Post postExiste = postService.obtenerPostId(id);
        postExiste.setEstado("0");

        postService.actualizarEstadoPost(postExiste);
        return "redirect:/posts";
    }

}

