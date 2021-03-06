package mops;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mops.fragen.MultipleChoiceFrage;
import mops.fragen.MultipleResponseFrage;
import mops.fragen.SingleResponseFrage;
import mops.fragen.TextFrage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FragebogenTemplateTest {
  private transient FragebogenTemplate template;

  @BeforeEach
  public void setUp() {
    template = new FragebogenTemplate("Test-Template");
  }

  @Test
  public void korrekteFrageWirdGeloescht() {
    TextFrage text = new TextFrage(1L, "Anmerkungen");
    SingleResponseFrage mc = new SingleResponseFrage(2L, "Wie geht's?");
    template.addFrage(mc);
    template.addFrage(text);

    template.deleteFrageById(1L);

    assertTrue(template.getFragen().contains(mc));
    assertFalse(template.getFragen().contains(text));
  }

  @Test
  public void korrekteMultipleChoiceFrageWirdGeloescht() {
    TextFrage text = new TextFrage(1L, "Anmerkungen");
    SingleResponseFrage mc = new SingleResponseFrage(2L, "Wie geht's?");
    MultipleResponseFrage mr = new MultipleResponseFrage(3L, "Welche Termine sind möglich");
    template.addFrage(mr);
    template.addFrage(mc);
    template.addFrage(text);

    MultipleChoiceFrage totest = template.getMultipleChoiceFrageById(2L);

    assertEquals(totest, mc);
  }
}
