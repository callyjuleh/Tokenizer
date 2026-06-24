package src.ui;

import src.model.Lexer;
import src.model.Token;
import src.model.TokenDatabase;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TokenizerUI extends JFrame {

    private static final Color BG_DARK = new Color(18, 18, 30);
    private static final Color BG_PANEL = new Color(28, 28, 45);
    private static final Color BG_INPUT = new Color(38, 38, 58);
    private static final Color ACCENT = new Color(99, 179, 237);
    private static final Color ACCENT2 = new Color(154, 117, 234);
    private static final Color GREEN = new Color(72, 199, 142);
    private static final Color RED_SOFT = new Color(252, 129, 129);
    private static final Color TEXT_MAIN = new Color(226, 232, 240);
    private static final Color TEXT_DIM = new Color(113, 128, 150);
    private static final Color BORDER_CLR = new Color(55, 55, 80);
    private static final Color ROW_ALT = new Color(34, 34, 52);
    private static final Color HEADER_BG = new Color(40, 40, 65);

    private static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);

    private final TokenDatabase tokenDB = new TokenDatabase();
    private final Lexer lexer = new Lexer(tokenDB);
    private final List<Token> tokens = new ArrayList<>();

    private JTextArea inputArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel countLabel;

    public TokenizerUI() {
        super("Tokenizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        bar.setPreferredSize(new Dimension(0, 58));

        JLabel title = new JLabel(" Tokenizer");
        title.setFont(FONT_TITLE);
        title.setForeground(ACCENT);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
        left.setOpaque(false);
        left.add(title);

        JButton tokenizeBtn = styledButton("\u25B6  Tokenize", ACCENT, BG_DARK);
        JButton clearBtn = styledButton("\u2715  Clear", TEXT_DIM, BG_DARK);
        tokenizeBtn.addActionListener(e -> doTokenize());
        clearBtn.addActionListener(e -> doClear());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 11));
        right.setOpaque(false);
        right.add(clearBtn);
        right.add(tokenizeBtn);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildInputPanel(), buildOutputPanel());
        split.setDividerLocation(430);
        split.setDividerSize(5);
        split.setBackground(BG_DARK);
        split.setBorder(null);
        split.setOneTouchExpandable(false);
        return split;
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR));

        panel.add(sectionLabel("\u270F  SOURCE CODE"), BorderLayout.NORTH);

        inputArea = new JTextArea();
        inputArea.setFont(FONT_MONO);
        inputArea.setForeground(TEXT_MAIN);
        inputArea.setBackground(BG_INPUT);
        inputArea.setCaretColor(ACCENT);
        inputArea.setSelectionColor(new Color(99, 179, 237, 60));
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        inputArea.setText("int x = 10 + 5;");
        inputArea.setTabSize(4);

        inputArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "tokenize");
        inputArea.getActionMap().put("tokenize", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doTokenize();
            }
        });

        panel.add(darkScroll(inputArea), BorderLayout.CENTER);

        JLabel hint = new JLabel("  Ctrl+Enter to tokenize");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_DIM);
        hint.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        hint.setBackground(BG_PANEL);
        hint.setOpaque(true);
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);

        String[] cols = { "#", "LEXEME", "TOKEN TYPE" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tokenTable = new JTable(tableModel);
        styleTable();

        countLabel = new JLabel("  Tokens: 0");
        countLabel.setFont(FONT_LABEL);
        countLabel.setForeground(ACCENT2);
        countLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        countLabel.setBackground(BG_PANEL);
        countLabel.setOpaque(true);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(sectionLabel("\u25C8  TOKEN RESULTS"), BorderLayout.WEST);
        top.add(countLabel, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(darkScroll(tokenTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(13, 13, 22));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
        bar.setPreferredSize(new Dimension(0, 26));

        statusLabel = new JLabel("  Ready. Enter code and press Tokenize.");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_DIM);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel credit = new JLabel("JavaTokenizer  |  Lexical Analyzer  ");
        credit.setFont(FONT_SMALL);
        credit.setForeground(new Color(60, 60, 90));
        bar.add(credit, BorderLayout.EAST);

        return bar;
    }

    private void doTokenize() {
        String src = inputArea.getText().trim();
        if (src.isEmpty()) {
            setStatus("\u26A0  No source code entered.", RED_SOFT);
            return;
        }

        tableModel.setRowCount(0);
        tokens.clear();
        tokens.addAll(lexer.tokenize(src));

        long invalidCount = tokens.stream()
                .filter(t -> t.getTokenType().equals("INVALID"))
                .count();

        int i = 1;
        for (Token t : tokens) {
            tableModel.addRow(new Object[] { i++, t.getLexeme(), t.getTokenType() });
        }

        countLabel.setText("  Tokens: " + tokens.size());

        if (invalidCount > 0) {
            setStatus("\u2714  Tokenized " + tokens.size() + " token(s). "
                    + invalidCount + " INVALID (not in DB).", RED_SOFT);
        } else {
            setStatus("\u2714  Tokenized " + tokens.size() + " token(s). All valid.", GREEN);
        }
    }

    private void doClear() {
        inputArea.setText("");
        tableModel.setRowCount(0);
        tokens.clear();
        countLabel.setText("  Tokens: 0");
        setStatus("  Cleared.", TEXT_DIM);
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText("  " + msg);
        statusLabel.setForeground(color);
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_DIM);
        lbl.setBackground(BG_PANEL);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
                BorderFactory.createEmptyBorder(8, 4, 8, 4)));
        return lbl;
    }

    private JButton styledButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.darker(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(fg.darker().darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private JScrollPane darkScroll(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_INPUT);
        return sp;
    }

    private void styleTable() {
        tokenTable.setBackground(BG_INPUT);
        tokenTable.setForeground(TEXT_MAIN);
        tokenTable.setFont(FONT_MONO);
        tokenTable.setRowHeight(26);
        tokenTable.setGridColor(BORDER_CLR);
        tokenTable.setShowGrid(true);
        tokenTable.setSelectionBackground(new Color(99, 179, 237, 50));
        tokenTable.setSelectionForeground(TEXT_MAIN);
        tokenTable.setFillsViewportHeight(true);
        tokenTable.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tokenTable.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(ACCENT);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setReorderingAllowed(false);

        tokenTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        tokenTable.getColumnModel().getColumn(0).setMaxWidth(50);
        tokenTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        tokenTable.getColumnModel().getColumn(2).setPreferredWidth(260);

        tokenTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                setBackground(sel ? new Color(99, 179, 237, 60) : (row % 2 == 0 ? BG_INPUT : ROW_ALT));
                setForeground(col == 2 ? colorForToken(val) : TEXT_MAIN);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return this;
            }
        });
    }

    private Color colorForToken(Object val) {
        if (val == null)
            return TEXT_MAIN;
        return switch (val.toString()) {
            case "KEYWORD" -> new Color(255, 165, 100);
            case "IDENTIFIER" -> new Color(154, 230, 180);
            case "LITERAL" -> new Color(252, 211, 77);
            case "ARITHMETIC OPERATOR" -> new Color(236, 135, 250);
            case "RELATIONAL OPERATOR" -> new Color(236, 135, 250);
            case "LOGICAL OPERATOR" -> new Color(236, 135, 250);
            case "ASSIGNMENT OPERATOR" -> new Color(113, 200, 240);
            case "DELIMITER" -> TEXT_DIM;
            case "COMMENT" -> new Color(100, 120, 100);
            case "INVALID" -> RED_SOFT;
            default -> TEXT_MAIN;
        };
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(TokenizerUI::new);
    }
}