package mops;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import mops.antworten.TextAntwort;
import mops.fragen.Frage;
import mops.fragen.MultipleChoiceFrage;
import mops.fragen.MultipleResponseFrage;
import mops.fragen.SingleResponseFrage;
import mops.fragen.TextFrage;
import mops.rollen.Dozent;
import org.junit.jupiter.api.Test;

public class DozentServiceTest {
  private final DozentService service = new DozentService();

  @Test
  public void textFrageWirdKorrektErzeugt() {
    String fragetyp = "textfrage";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof TextFrage);
  }

  @Test
  public void singleResponseFrageWirdKorrektErzeugt() {
    String fragetyp = "multiplechoice";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof SingleResponseFrage);
  }

  @Test
  public void multipleResponseFrageWirdKorrektErzeugt() {
    String fragetyp = "multipleresponse";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof MultipleResponseFrage);
  }

  @Test
  public void korrekteFrageWirdZurueckGegeben() {
    Fragebogen fragebogen = new Fragebogen("Analysis I", "Heinz Mustermann");
    TextFrage frage = new TextFrage(1L, "Wie geht's?");
    TextFrage frage2 = new TextFrage(2L, "Sind sie zufrieden mit der Veranstaltung?");
    fragebogen.addFrage(frage);
    fragebogen.addFrage(frage2);

    Frage totest = service.getFrage(1L, fragebogen);

    assertEquals(totest, frage);
  }

  @Test
  public void korrekteTextAntwortWirdZurueckGegeben() {
    TextAntwort antwort = new TextAntwort(1L, "Sehr gut");
    TextAntwort antwort2 = new TextAntwort(2L, "Sehr schlecht");
    TextFrage frage = new TextFrage(1L, "Wie geht's?");
    frage.addAntwort(antwort);
    frage.addAntwort(antwort2);
    TextFrage frage2 = new TextFrage(2L, "Sind Sie zufrieden?");
    Fragebogen fragebogen = new Fragebogen("Analysis I", "Heinz Mustermann");
    fragebogen.addFrage(frage2);
    fragebogen.addFrage(frage);

    TextAntwort totest = service.getTextAntwort(1L, 2L, fragebogen);

    assertEquals(totest, antwort2);
  }

  @Test
  public void korrekteMultipleChoiceFrageWirdZurueckGegeben() {
    Fragebogen fragebogen = new Fragebogen("Analysis I", "Heinz Mustermann");
    TextFrage frage = new TextFrage(1L, "Wie geht's?");
    MultipleChoiceFrage frage2 =
        new MultipleChoiceFrage(2L, "Die Vorlesung ist strukturiert", false);
    MultipleChoiceFrage frage3 = new MultipleChoiceFrage(3L, "Der Dozent ist motiviert", false);
    fragebogen.addFrage(frage);
    fragebogen.addFrage(frage2);
    fragebogen.addFrage(frage3);

    Frage totest = service.getMultipleChoiceFrage(2L, fragebogen);

    assertEquals(totest, frage2);
  }

  @Test
  public void alleFrageboegenWerdenEingesammelt() {
    Fragebogen fragebogen = new Fragebogen("Analysis I", "Heinz Mustermann");
    Fragebogen fragebogen2 = new Fragebogen("Tutorium zur Analysis I", "Heinz Mustermann");
    Veranstaltung analysis = new Veranstaltung("Analysis I", "WiSe 2010", new Dozent("heinz001"));
    analysis.addFragebogen(fragebogen);
    analysis.addFragebogen(fragebogen2);
    Fragebogen fragebogen3 = new Fragebogen("Programmierung", "Heinz Mustermann");
    Veranstaltung prog = new Veranstaltung("Programmierung", "WiSe 2010", new Dozent("heinz001"));
    prog.addFragebogen(fragebogen3);
    List<Veranstaltung> veranstaltungen = List.of(analysis, prog);
    List<Fragebogen> frageboegen = List.of(fragebogen, fragebogen2, fragebogen3);

    List<Fragebogen> totest = service.holeFrageboegenVomDozent(veranstaltungen);

    assertEquals(totest, frageboegen);
  }
}
