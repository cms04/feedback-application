package mops.controllers;

import javax.annotation.security.RolesAllowed;
import mops.DozentService;
import mops.FragebogenTemplate;
import mops.TypeChecker;
import mops.fragen.Auswahl;
import mops.fragen.MultipleChoiceFrage;
import mops.rollen.Dozent;
import mops.security.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/feedback/dozenten/templates")
public class DozentTemplateController {
  private static final String REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES =
      "redirect:/feedback/dozenten/templates/";
  private static final String ORGA_ROLE = "ROLE_orga";

  private final transient DozentService dozentservice;
  private final transient TypeChecker typechecker;
  private final transient VeranstaltungsRepository veranstaltungen;

  public DozentTemplateController(mops.database.VeranstaltungsRepository veranstaltungen) {
    this.veranstaltungen = veranstaltungen;
    dozentservice = new DozentService();
    typechecker = new TypeChecker();
  }

  @GetMapping("")
  @RolesAllowed(ORGA_ROLE)
  public String getTemplatePage(Model model, KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    model.addAttribute("templates", dozent.getTemplates());
    model.addAttribute("account", createAccountFromPrincipal(token));
    return "dozenten/templates";
  }

  @PostMapping("")
  @RolesAllowed(ORGA_ROLE)
  public String neuesTemplate(String templatename, KeycloakAuthenticationToken token) {
    FragebogenTemplate template = new FragebogenTemplate(templatename);
    Dozent dozent = getDozentFromToken(token);
    dozent.addTemplate(template);
    return REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES + template.getId();
  }

  @GetMapping("/{templatenr}")
  @RolesAllowed(ORGA_ROLE)
  public String templateBearbeitung(@PathVariable Long templatenr,
                                    KeycloakAuthenticationToken token, Model model) {
    Dozent dozent = getDozentFromToken(token);
    model.addAttribute("template", dozent.getTemplateById(templatenr));
    model.addAttribute("typechecker", typechecker);
    model.addAttribute("account", createAccountFromPrincipal(token));
    return "dozenten/edittemplate";
  }

  @PostMapping("/{templatenr}")
  @RolesAllowed(ORGA_ROLE)
  public String neueFrage(@PathVariable Long templatenr, KeycloakAuthenticationToken token,
                          String fragetyp, String fragetext) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    template.addFrage(dozentservice.createNeueFrageAnhandFragetyp(fragetyp, fragetext));
    return REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES + templatenr;
  }

  @GetMapping("/{templatenr}/{fragennr}")
  @RolesAllowed(ORGA_ROLE)
  public String editMultipleChoiceQuestion(@PathVariable Long templatenr,
                                           @PathVariable Long fragennr, KeycloakAuthenticationToken token, Model model) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    MultipleChoiceFrage frage = template.getMultipleChoiceFrageById(fragennr);
    model.addAttribute("account", createAccountFromPrincipal(token));
    model.addAttribute("frage", frage);
    model.addAttribute("template", templatenr);
    return "dozenten/mcedit-template";
  }

  @PostMapping("/{templatenr}/{fragennr}")
  @RolesAllowed(ORGA_ROLE)
  public String newMultipleChoiceAnswer(@PathVariable Long templatenr, @PathVariable Long fragennr,
                                        KeycloakAuthenticationToken token, String antworttext) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    MultipleChoiceFrage frage = template.getMultipleChoiceFrageById(fragennr);
    frage.addChoice(new Auswahl(antworttext));
    return REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES + templatenr + "/" + fragennr;
  }

  @PostMapping("/delete/{templatenr}")
  @RolesAllowed(ORGA_ROLE)
  public String deleteTemplate(@PathVariable Long templatenr, KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    dozent.deleteTemplateById(templatenr);
    return "redirect:/feedback/dozenten/templates";
  }

  @PostMapping("/delete/{templatenr}/{fragennr}")
  @RolesAllowed(ORGA_ROLE)
  public String deleteFrage(@PathVariable Long templatenr, @PathVariable Long fragennr,
                            KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    template.deleteFrageById(fragennr);
    return REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES + templatenr;
  }

  @PostMapping("/delete/{templatenr}/{fragennr}/{auswahlnr}")
  @RolesAllowed(ORGA_ROLE)
  public String deleteAntwortmoeglichkeit(@PathVariable Long templatenr,
                                          @PathVariable Long fragennr, @PathVariable Long auswahlnr,
                                          KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    MultipleChoiceFrage frage = template.getMultipleChoiceFrageById(fragennr);
    frage.deleteChoice(auswahlnr);
    return REDIRECT_FEEDBACK_DOZENTEN_TEMPLATES + templatenr + "/" + fragennr;
  }

  private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(principal.getName(),
        principal.getKeycloakSecurityContext().getIdToken().getEmail(), null,
        token.getAccount().getRoles());
  }

  private Dozent getDozentFromToken(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return veranstaltungen.getDozentByUsername(principal.getName());
  }
}
