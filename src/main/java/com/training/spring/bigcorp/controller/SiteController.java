package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.config.SecurityConfig;
import com.training.spring.bigcorp.exception.NotFoundException;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

/**
 * Controleur des Sites
 */
@Controller
@RequestMapping("/sites")
@Transactional
public class SiteController {

    /**
     * Dao Site
     */
    @Autowired
    private SiteDao siteDao;

    /**
     * Dao Captor
     */
    @Autowired
    private CaptorDao captorDao;

    /**
     * Dao Measure
     */
    @Autowired
    private MeasureDao measureDao;

    /**
     * Donne la listes des différents sites
     * @return
     */
    @GetMapping
    public ModelAndView list(){
        return new ModelAndView("sites").addObject("sites",siteDao.findAll());
    }

    /**
     * Donne accès à un site en particulier
     * @param id id du site
     * @return
     */
    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String id){
        return new ModelAndView("site")
                .addObject("site",siteDao.findById(id).orElseThrow(NotFoundException::new));
    }

    /**
     * Permet de créer un nouveau site
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(){
        return new ModelAndView("site")
                .addObject("site", new Site());
    }

    /**
     * Permet d'enregistrer un nouveau site
     * @param site
     * @return
     */
    @Secured(SecurityConfig.ROLE_ADMIN)
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(Site site){
        // Si pas d'Id => Mode Création
        if(site.getId() == null){
            return new ModelAndView("site")
                    .addObject("site", siteDao.save(site));
        }
        // Sinon => Mode Modification
        else{
            // On charge l'entité correspondant à l'Id
            Site siteToPersist = siteDao.findById(site.getId()).orElseThrow(NotFoundException::new);
            // L'utilisateur ne peut chager que le nom du site sur l'écran
            siteToPersist.setName(site.getName());
            // Comme on est en contexte transitionnel => Pas besoin d'appeler save - l'object est automatiquement persisté
            return new ModelAndView("sites")
                    .addObject("sites",siteDao.findAll());
        }
    }

    /**
     * Permet de supprimer un site
     * @param id id du site à supprimer
     * @return
     */
    @Secured(SecurityConfig.ROLE_ADMIN)
    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String id){
        /* Comme les capteurs sont liés à un site et les mesures sont liés à un capteur
         Nous devons faire le ménage pour ne pas avoir d'erreurs à la suppression d'un site utilisé ailleurs dans la base*/
        Site site = siteDao.findById(id).orElseThrow(NotFoundException::new);
        // Suppressions des mesures
        site.getCaptors().forEach(c->measureDao.deleteByCaptorId(c.getId()));
        // Suppression des capteurs
        captorDao.deleteBySiteId(id);
        siteDao.delete(site);
        return new ModelAndView("sites")
                .addObject("sites",siteDao.findAll());
    }

    /**
     * Permet de trouver une liste de mesures en fonction d'un site
     * @param id id du site
     * @return
     */
    @GetMapping("/{id}/measures")
    public ModelAndView findMeasuresById(@PathVariable String id) {
        Site site = siteDao.findById(id).orElseThrow(NotFoundException::new);
       /* Comme les templates ont une intelligence limitée on concatène ici les id de
        captor dans une chaine de caractères qui pourra être exeploitée tel quelle */
        String captors = site.getCaptors()
                .stream()
                .map(c -> "{ id: '" + c.getId() + "', name: '" + c.getName()
                        + "'}")
                .collect(Collectors.joining(","));

        return new ModelAndView("site-measures")
                .addObject("site", site)
                .addObject("captors", captors);
    }
}
