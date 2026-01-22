import java.awt.*;
// Темы (простая модель: фон, панель, текст, кнопка)
public class Theme {
    Color background;
    Color panel;
    Color text;
    Color button;
    Color listAlt1;
    Color listAlt2;
    Color listSelection;
    Theme(Color background, Color panel, Color text, Color button,
          Color listAlt1, Color listAlt2, Color listSelection) {
        this.background = background;
        this.panel = panel;
        this.text = text;
        this.button = button;
        this.listAlt1 = listAlt1;
        this.listAlt2 = listAlt2;
        this.listSelection = listSelection;
    }
}
