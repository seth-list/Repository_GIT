import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import com.toedter.calendar.JDateChooser;
import org.eclipse.jgit.lib.Repository;

/**
 * @author seth-list
 * Main working class for Project
 * create Panel and Performin operations
 */
public class GitForm extends JFrame
{
    //создание основной панели
    private JPanel content = new JPanel();

    //кнопки управления
    private JButton OpenRep = new JButton("Хранилище");
    private JButton OpenProject = new JButton("Проект");
    private JButton OpenUser = new JButton("Пользователь");

    //модели таблицы
    private MyTable3 table = new MyTable3();
    private MyTable3 tableRepo = new MyTable3();
    private MyTable3 tableRes = new MyTable3();
    private MyTable3 tableWRes = new MyTable3();
    private MyTable3 tableTask = new MyTable3();
    private MyTable3 tableCommit = new MyTable3();

    private boolean add = false;
    private boolean addInfo = false;
    private boolean addClone = false;
    private boolean tabRepo = false;
    private boolean tabRes = false;
    private boolean addUser = false;
    private boolean addTask = false;
    private boolean addWork = false;
    private boolean addCommit = false;
    private boolean addnewCommit = false;
    private boolean addBranch = false;

    private String fileDir = "";
    private String fileWeb = "";
    private String typeRepo = "";

    //модель cb status
    private DefaultComboBoxModel<String> cbStatModel = new DefaultComboBoxModel<String>();
    //модель cb status
    private DefaultComboBoxModel<String> cbSModel = new DefaultComboBoxModel<String>();
    //модель cb status
    private DefaultComboBoxModel<String> cbBranchModel = new DefaultComboBoxModel<String>();
    //ресурсы
    private DefaultComboBoxModel<String> modelRes = new DefaultComboBoxModel<String>();
    //инициализация модели
    private DefaultComboBoxModel<String> modelTask1 = new DefaultComboBoxModel<String>();
    //инициализация модели
    private DefaultComboBoxModel<String> modelTask2 = new DefaultComboBoxModel<String>();
    //инициализация модели
    private DefaultComboBoxModel<String> listCommit = new DefaultComboBoxModel<String>();
    //инициализация модели
    private DefaultComboBoxModel<String> listBranch = new DefaultComboBoxModel<String>();
    //стадия проекта
    private DefaultComboBoxModel<String> cbKModel = new DefaultComboBoxModel<String>();


    //==========================
    private JCheckBox checkBox = new JCheckBox();

    //панель под таблицу
    private JPanel TablePanel = new JPanel();
    //панель под кнопки
    private JPanel ButtonPanel = new JPanel();

    //класс sql
    private sqlGit sql = new sqlGit();

    //информация о хранилищах
    private int numCommon = 0;
    private int numLocal = 0;
    private int numGlobal = 0;
    //значения
    private JLabel[] labelsV =  new JLabel[3];

    private JButton CreateRep = new JButton("Панель добавления");
    private JButton CloneRepo = new JButton("Панель копирования");
    private JButton ShowInfo = new JButton("Показать информацию");
    private JButton OpenTWR = new JButton("Открыть проект");

    private JButton NewTask = new JButton("Новая задача");
    private JButton NewWork = new JButton("Новая работа");
    private JButton NewUser = new JButton("Новый пользователь");

    private JButton NewBranch = new JButton("Новая ветка");
    private JButton btBranch = new JButton("Создание ветки");
    private JButton btCommit = new JButton("Создание события");


    private JButton bLCommit = new JButton("Список событий");
    private JButton bLBranch = new JButton("Список веток");

    //вспомогательные панели
    private JPanel ShowTWR = new JPanel();
    private JPanel AddTWR = new JPanel();

    private JPanel TableRes     = new JPanel();
    private JPanel TablePanelCM    = new JPanel();

    //control panel
    private JPanel jControlBranch  = new JPanel();
    private JPanel jControlCommit  = new JPanel();


    private boolean repoNew = true;


    //раскрывающийся список с commit
    private JComboBox<String> cbCommit = new JComboBox<String>(listCommit);
    //раскрывающийся список с branch
    private JComboBox<String> cbBranch = new JComboBox<String>(listBranch);

    //текущее хранилище
    private Repository repo;

    //имя текущей ветки
    private String nameBranch;

    //пользователь текущей работы
    private String nameManager;

    //HEAD
    private JTextField masterHEAD = new JTextField();

    /**
     *  Constructor
     *  All in program creating in one time
     */
    public GitForm()
    {

        super("Контроль версий"); //заголовок
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  //действие по закрытию - закрыть JFrame

        //установить рамку для основной панели
        content.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        //пустая и опущенная рамка
        Border EmBorder = BorderFactory.createEmptyBorder(12,12,12,12);
        Border LowBorder = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(LowBorder, EmBorder);

        //создание двойной рамки
        Border EtchBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        final Border compound2 = BorderFactory.createCompoundBorder(EtchBorder, EmBorder);


        Border EmBorder2 = BorderFactory.createEmptyBorder(4,4,4,4);
        Border EtchBorder2 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border compound3 = BorderFactory.createCompoundBorder(EtchBorder2, EmBorder2);

        //для расширения раскр списка
        BoundPopupMenuListener listener = new BoundPopupMenuListener(true, false);
        cbCommit.addPopupMenuListener(listener);
        cbCommit.setMaximumSize(new Dimension(250,25));

        //не активна HEAD
        masterHEAD.setEditable(false);

        //логин и пароль с кнопкой
        //==========================================
        JLabel loginLabel = new JLabel("Логин:");
        JLabel passLabel = new JLabel("Пароль:");


        final JTextField loginText = new JTextField("User");
        loginText.setBorder(LowBorder);
        loginText.setPreferredSize(new Dimension(50,15));
        final JPasswordField passText = new JPasswordField();
        passText.setBorder(LowBorder);
        passText.setPreferredSize(new Dimension(50,15));


        JButton okButton = new JButton("ОК");
        okButton.setPreferredSize(new Dimension(100, 15));
        okButton.setToolTipText("Введите логин и пароль менеджера проекта");
        //==========================================


        //кнопка ОК обработчик
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String login, pass;
                //установка логина
                if(loginText.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(),
                            "Логин не введен!");
                    return;
                }
                else
                {
                    login = loginText.getText();
                }

                //установка пароля
                if(passText.getPassword().length == 0)

                {
                    JOptionPane.showMessageDialog(new JPanel(), "Пароль не введен!");
                    return;
                }
                else
                {
                    pass = new String(passText.getPassword());
                }
                //если логин и пароль правильные
                if(sql.setLoginPass(login, pass))
                {

                    //JOptionPane.showMessageDialog(new JPanel(), "Соединение установлено!");

                    //флаг, что нужна проверка существования хранилищ при первом запуске
                    sql.setNeed(true);
                    //изменить таблицу в соответствии с данными
                    ChangeTable(table, 0);
                    sql.setNeed(false);

                    //добавление панелей в центр с таблице
                    //и на верхнюю часть с кнопками
                    content.removeAll();
                    content.setLayout(new BorderLayout());
                    content.add(TablePanel, BorderLayout.CENTER);
                    content.add(ButtonPanel, BorderLayout.NORTH);

                    //сделать активными кнопки
                    OpenRep.setEnabled(true);
                    OpenProject.setEnabled(true);
                    OpenUser.setEnabled(true);

                    content.repaint();
                    content.validate();
                }
                else
                {
                    JOptionPane.showMessageDialog(new JPanel(),
                            "Пароль или логин неправильны!");
                }
            }
        });

        //панель под логин и пароль
        JPanel loginPass = new JPanel();
        loginPass.setBorder(compound);
        loginPass.setLayout(new GridLayout(7, 1));
        loginPass.add(loginLabel);
        loginPass.add(loginText);
        loginPass.add(new JPanel());
        loginPass.add(passLabel);
        loginPass.add(passText);
        //кнопка
        loginPass.add(new JPanel());

        //кнопка выбора
        JPanel bs = new JPanel();
        bs.add(okButton);

        loginPass.add(bs);


        //таблица проектов
        //======================================================================
        ArrayList<String> colNP = new ArrayList<String>();

        colNP.clear();
        colNP.add("Название проекта");
        colNP.add("Ответственный");
        colNP.add("Дата начала");
        colNP.add("Дата окончания");
        colNP.add("Краткое описание");
        colNP.add("Бюджет");
        colNP.add("Хранилище");

        //======================================================================
        //установить заголовок
        table.ChColumn(colNP);
        //таблица
        final JTable tableJ = new JTable(table);
        //сортировка
        tableJ.setRowSorter(new TableRowSorter<MyTable3>(table));

        //таблица во всю панель
        tableJ.setFillsViewportHeight(true);

        //scrollPane - таблица
        final JScrollPane scrollPane = new JScrollPane(tableJ);

        //=============================================
        //обработчик выделения строки таблицы
        tableJ.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent event) {
                if(tableJ.getSelectedRow()!=-1)
                {
                    //0 - для таблицы проекта
                    getIDRow(tableJ, table, 0);
                    OpenTWR.setEnabled(true);
                }
                else
                {
                    sql.setIdPr(0);
                    OpenTWR.setEnabled(false);
                }
            }
        });





        //таблица хранилищ
        ArrayList<String> colPN = new ArrayList<String>();

        colPN.clear();
        colPN.add("Название хранилища");
        colPN.add("Путь к хранилищу");
        colPN.add("Название проекта");
        colPN.add("Тип хранилища");
        colPN.add("Комментарий");

        //установить заголовок
        tableRepo.ChColumn(colPN);
        //таблица
        final JTable tableR = new JTable(tableRepo);
        //сортировка
        tableR.setRowSorter(new TableRowSorter<MyTable3>(tableRepo));

        //таблица во всю панель
        tableR.setFillsViewportHeight(true);

        //scrollPane - таблица
        final JScrollPane scrollPaneR = new JScrollPane(tableR);
        //====================================================

//===================================================================
//Ресурсы
//==========создание таблицы ресурсов=================
        //создание заголовка таблицы фирм
        ArrayList<String> colNRes = new ArrayList<String>();
        //{""};
        colNRes.clear();
        colNRes.add("Имя пользователя");
        colNRes.add("Ставка");
        colNRes.add("Логин пользователя");


        //панель под таблицы
        //модель таблицы
        //установить заголовок
        tableRes.ChColumn(colNRes);
        //таблица
        final JTable tableRs = new JTable(tableRes);
        //сортировка
        tableRs.setRowSorter(new TableRowSorter<MyTable3>(tableRes));

        //запись в панель под таблицы
        CreateTable(tableRs, TableRes);
//========== окончание создания таблицы ресурсов=================
//====================================================================

//====================================================================
//панель для ввода данных о ресурсах
//======================================================================
        final JPanel jDataRes =  new JPanel();

        //поля
        final ArrayList<String> pole5 = new ArrayList<String>();
        //текстовое поле
        pole5.add("T");
        pole5.add("N");
        pole5.add("T");
        pole5.add("T");


        final ArrayList<String> data5 = new ArrayList<String>();
        data5.add("Имя пользователя");
        data5.add("Ставка");
        data5.add("Логин");
        data5.add("Пароль");



        final int[] rest5 = new int[3];
        rest5[0] = 50;
        rest5[1] = 50;
        rest5[2] = 50;

//=============================
//===окончание ввода ресурсов=============================================

//работа с ресурсами
//======================================================================
//создание заголовка таблицы работ с клиентом
        ArrayList<String> colNResWork = new ArrayList<String>();
        //{""};
        colNResWork.clear();
        colNResWork.add("Дата начала работы");
        colNResWork.add("Дата окончания работы");
        colNResWork.add("Название работы");
        colNResWork.add("Статус работы");
        colNResWork.add("Результат работы");
        colNResWork.add("Пользователь");
        colNResWork.add("Задача");
        colNResWork.add("Commit");

        //панель под таблицы
        //модель таблицы
        //установить заголовок
        tableWRes.ChColumn(colNResWork);
        //таблица
        final JTable tableResWork = new JTable(tableWRes);
        //сортировка
        tableResWork.setRowSorter(new TableRowSorter<MyTable3>(tableWRes));
        //запись в панель под таблицы
        JPanel tableResWork1 = new JPanel();
        CreateTable(tableResWork, tableResWork1);
//======================================================================
//========== окончание создания таблицы работ с ресурсами=================

        //выделение таблицы
        tableResWork.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int InRow = tableResWork.getSelectedRow();
                //проверка что есть выделение
                if(InRow == -1)
                {
                    return;
                }

                if (InRow > -1)
                {
                    tableResWork.setRowSelectionInterval(InRow, InRow);
                }

                int row = tableResWork.convertRowIndexToModel(InRow);

                if(repo == null)
                return;

                //получить выделенную строку
                ArrayList<Object> Row = tableWRes.getRowAt(row);

                //имя пользователя выполняющего работы
                sql.nameRes = Row.get(5).toString();


                //получение данных об этом пользователе
                try
                {
                    sql.idManager = (Integer)sql
                            .selectValue(10).get(0).get(0);
                    nameManager = sql.selectValue(1).get(0).get(3).toString();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                int id;

                //получение имени ветки
                nameBranch = "";

                //id Работы
                getIDRow(tableResWork, tableWRes, 4);

                try
                {
                    id = (Integer)sql.selectValue(41).get(0).get(9);
                    sql.setIdTask(id);
                    //получение имени ветки по id задачи
                    nameBranch = sql.selectValue(30).get(0).get(3).toString();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


                if(nameBranch.equals("Нет ветки"))
                return;

                //получение списка
                ArrayList<ArrayList<Object>> RowStr
                        = gitSend.listCommit(repo, nameBranch,
                        nameManager);

                int i = 0;
                //добавление в model combobox
                listCommit.removeAllElements();
                if(RowStr.size()!=0)
                {
                    while(i<RowStr.size())
                    {
                        listCommit.addElement(RowStr.get(i).get(0).toString());
                        i++;
                    }
                }
            }
        });




//======================================================================
        //панель под данные
        final JPanel jDataResWork =  new JPanel();

        //поля
        final ArrayList<String> pole9 = new ArrayList<String>();
        //текстовое поле, две даты, два текстовое поле
        pole9.add("D");
        pole9.add("D");
        pole9.add("T");
        pole9.add("CR");//CS
        pole9.add("T");
        pole9.add("C");//C
        pole9.add("C");


        final ArrayList<String> data9 = new ArrayList<String>();
        data9.add("Дата начала работы");
        data9.add("Дата окончания работы");
        data9.add("Название работы");
        data9.add("Статус работы");
        data9.add("Результат работы");
        data9.add("Пользователь");
        data9.add("Задача");


        final int[] rest9 = new int[3];
        rest9[0] = 50;
        rest9[1] = 50;
        rest9[2] = 30;
//=============================
//===окончание ввода работ=============================================
//===================================================================
//Задачи
//==========создание таблицы задач=================
        //создание заголовка таблицы задач
        ArrayList<String> colNTask = new ArrayList<String>();
        //{""};
        colNTask.clear();
        colNTask.add("Название задачи");
        colNTask.add("Дата начала выполнения");
        colNTask.add("Дата окончания выполнения");
        colNTask.add("Краткое описание");
        colNTask.add("Ответственный");
        colNTask.add("Статус выполнения");
        colNTask.add("Название ветки");

        //панель под таблицы
        //модель таблицы
        //установить заголовок
        tableTask.ChColumn(colNTask);
        //таблица
        final JTable tableTk = new JTable(tableTask);
        //сортировка
        tableTk.setRowSorter(new TableRowSorter<MyTable3>(tableTask));
        //запись в панель под таблицы
        JPanel tableTask1 = new JPanel();
        CreateTable(tableTk, tableTask1);
//========== окончание создания таблицы задач=================
//====================================================================


        cbKModel.addElement("Испытания");
        cbKModel.addElement("Выполнение");
        cbKModel.addElement("Завершение");
//панель для ввода данных о задачах
//======================================================================
        //панель под данные
        final JPanel jDataTask =  new JPanel();

        //поля
        final ArrayList<String> pole8 = new ArrayList<String>();
        //текстовое поле, две даты, два текстовое поле
        pole8.add("T");
        pole8.add("D");
        pole8.add("D");
        pole8.add("T");
        pole8.add("C");
        pole8.add("CK");
        //pole8.add("T");

        final ArrayList<String> data8 = new ArrayList<String>();
        data8.add("Название задачи");
        data8.add("Дата начала выполнения");
        data8.add("Дата окончания выполнения");
        data8.add("Краткое описание");
        data8.add("Ответственный");
        data8.add("Статус задачи");
        //data8.add("Название ветки");

        final int[] rest8 = new int[4];
        rest8[0] = 50;
        rest8[1] = 100;
        rest8[2] = 50;
        //rest8[3] = 50;

//=============================
//===окончание ввода задач=============================================

            cbSModel.addElement("Первичный контакт");
            cbSModel.addElement("Переговоры");
            cbSModel.addElement("Принятие решение");
            cbSModel.addElement("Согласование договора");
            cbSModel.addElement("Успешно реализовано");
            cbSModel.addElement("Закрыто и не реализовано");

//===================================================================

        //создание вкладок для модуля задач/работ и ресурсов
        final JTabbedPane tabbedPaneRes = new JTabbedPane();

        tabbedPaneRes.addTab("Задачи", tableTask1);
        tabbedPaneRes.addTab("Работа с ресурсами", tableResWork1);
        //tabbedPaneRes.addTab("Пользователи", TableRes);

        //кнопка ресурс
        OpenTWR.setEnabled(false);


//====================================================================

        //на панель с таблицами разместить вкладки по центру
        TablePanel.setLayout(new BorderLayout());
        TablePanel.add(scrollPane, BorderLayout.CENTER);

        NewTask.setToolTipText("Создать новую задачу в проекте");
        bLBranch.setToolTipText("Показать список веток в хранилище");
        JButton changeHEAD = new JButton("Наследуемая ветка");
        changeHEAD.setToolTipText("Поменять ветку, которая унаследуется при создании новой ветки");
        masterHEAD.setToolTipText("Ветка, которая унаследуется при создании новой ветки");
        NewBranch.setToolTipText("Создание новой ветки для выбранной задачи");
        bLCommit.setToolTipText("Список совершенных коммитов в пределах текущей ветки выбранной задачи");
        JButton addBranch1 = new JButton("Добавить ветку");
        addBranch1.setToolTipText("Привязать существующую ветку к выбранной задаче");
        cbBranch.setToolTipText("Выбрать ветку для выполнения");

        NewWork.setToolTipText("Создать новую работу в задаче");
        JButton newCommit = new JButton("Новое событие");
        newCommit.setToolTipText("Создать коммит для текущей работы");
        JButton addCommit1 = new JButton("Добавить событие");
        addCommit1.setToolTipText("Привязать уже существующий коммит к данной работе");
        cbCommit.setToolTipText("Выбрать коммит для привязки");

        NewUser.setToolTipText("Панель создания нового пользователя");

        //обработчик кнопки "Открыть проект"
        OpenTWR.setToolTipText("Открыть выбранный проект для редактирования");
        OpenTWR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //добавить на форму панель проектов
                    content.removeAll();
                    tabbedPaneRes.setSelectedIndex(0);

                    //новый массив строк
                    ArrayList<ArrayList<Object>> RowStr;
                    //инициализация
                    RowStr = new ArrayList<ArrayList<Object>>();

                    try
                    {
                        RowStr = sql.selectValue(3); //получение sql запроса
                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    int i = 0;

                    //добавление в model combobox
                    modelTask2.removeAllElements();
                    modelTask1.removeAllElements();
                    if(RowStr.size()!=0)
                    {
                        while(i<RowStr.size())
                        {
                            modelTask2.addElement(RowStr.get(i).get(0).toString());
                            modelTask1.addElement(RowStr.get(i).get(0).toString());
                            i++;
                        }
                    }
                    modelTask2.addElement("Нет задачи");

                    try
                    {
                        RowStr = sql.selectValue(2); //получение sql запроса
                    }
                    catch (SQLException e1)
                    {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    i = 0;

                    //добавление в model combobox
                    modelRes.removeAllElements();
                    if(RowStr.size()!=0)
                    {
                        while(i<RowStr.size())
                        {
                            modelRes.addElement(RowStr.get(i)
                                    .get(0).toString());
                            i++;
                        }
                    }

                    ArrayList<Object> project;

                    //получение репозитория
                    String pathRepo = null;
                    String nameRepo = null;
                    try
                    {
                        project = sql.selectValue(222).get(0);
                        pathRepo = project.get(7).toString();
                        nameRepo = project.get(6).toString();
                    }
                    catch (SQLException e1)
                    {
                        JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
                    }


                    if(nameRepo.contains("(Копия)"))
                    {
                        nameRepo = nameRepo.replace("(Копия)", "");
                    }
                    //установление директории репозитория
                    File targetDir = new File(pathRepo+"\\"
                            +nameRepo+"\\.git");

                    if(!pathRepo.equals(""))
                    //получение хранилища
                    repo = gitSend.setRepo(targetDir);
                    else
                    repo = null;

                    listBranch.removeAllElements();
                    listCommit.removeAllElements();
                    //если не получилось создание вернуть
                    if(repo != null)
                    {
                        //получение списка веток
                        ArrayList<Object> bch = gitSend.getListBranch(repo);

                        i = 0;
                        //добавление в model combobox
                        if(bch.size()!=0)
                        {
                            while(i<bch.size())
                            {
                                listBranch.addElement(bch.get(i).toString());
                                i++;
                            }
                        }
                    }


                    masterHEAD.setText("");

                    //HEAD поменять на мастера
                    if(repo != null)
                    {
                        //HEAD поменять на мастера
                        masterHEAD.setText("refs/heads/master");
                        gitSend.chooseMaster(repo);
                    }

                    ButtonPanel.removeAll();
                    AddTWR.removeAll();
                    //=========================
                    AddTWR.setLayout(new GridLayout(1, 4, 6, 6));

                    AddTWR.add(NewTask);
                    AddTWR.add(btBranch);
                    AddTWR.add(bLCommit);
                    AddTWR.add(new JPanel());

                    ButtonPanel.add(AddTWR);


                    content.add(tabbedPaneRes, BorderLayout.CENTER);
                    content.add(ButtonPanel, BorderLayout.NORTH);

                    //обновить задачи
                    ChangeTable(tableTask, 3);

                    //ресурсов в задаче(работ)
                    ChangeTable(tableWRes, 4);


                    addInfo = false;
                    addClone = false;
                    add = false;
                    CloneRepo.setText("Панель копирования");
                    CreateRep.setText("Панель добавления");
                    ShowInfo.setText("Показать информацию");
                    content.repaint();
                    content.validate();
            }
        });



        //панель работы с ветками
        jControlBranch.setLayout(new GridLayout(2, 4, 10, 10));
        jControlBranch.setBorder(compound);

        //Добавление кнопок на панель создания ветки
        jControlBranch.add(NewBranch);
        jControlBranch.add(new JPanel());
        jControlBranch.add(addBranch1);
        jControlBranch.add(changeHEAD);
        jControlBranch.add(new JPanel());
        jControlBranch.add(new JPanel());
        jControlBranch.add(cbBranch);
        jControlBranch.add(masterHEAD);

        //панель работы с событиями
        jControlCommit.setLayout(new GridLayout(1, 4, 10, 10));
        jControlCommit.setBorder(compound);

        //Добавление кнопок на панель создания ветки
        jControlCommit.add(newCommit);
        jControlCommit.add(new JPanel());
        jControlCommit.add(addCommit1);
        jControlCommit.add(cbCommit);



        //TODO панель для работы с веткой
        btBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!addBranch)
                {
                    content.remove(jDataTask);
                    content.add(jControlBranch, BorderLayout.SOUTH);
                    addBranch = true;
                    addTask = false;
                    NewTask.setText("Новая задача");
                    btBranch.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jControlBranch);
                    btBranch.setText("Создание ветки");
                    addBranch = false;
                }
                content.repaint();
                content.validate();
            }
        });

        btCommit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!addnewCommit)
                {
                    content.remove(jDataResWork);
                    content.add(jControlCommit, BorderLayout.SOUTH);
                    addnewCommit = true;
                    addWork = false;
                    NewWork.setText("Новая работа");
                    btCommit.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jControlCommit);
                    btCommit.setText("Создание события");
                    addnewCommit = false;
                }
                content.repaint();
                content.validate();
            }
        });





        //панель задач изменение кнопок
        tabbedPaneRes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                content.remove(ButtonPanel);
                NewTask.setText("Новая задача");
                NewWork.setText("Новая работа");
                btCommit.setText("Создание события");
                btBranch.setText("Создание ветки");
                bLCommit.setText("Список событий");
                addnewCommit = false;
                //addUser = false;
                addTask = false;
                addWork = false;
                addCommit = false;
                addnewCommit = false;
                addBranch = false;


                content.add(ButtonPanel, BorderLayout.NORTH);

                //удалить все панели при перемещении
                content.remove(jDataRes);
                content.remove(jDataTask);
                content.remove(jDataResWork);
                content.remove(jControlCommit);
                content.remove(jControlBranch);

                ButtonPanel.removeAll();

                if(tabbedPaneRes.getSelectedIndex()==0)
                {

                    AddTWR.removeAll();
                    ShowTWR.removeAll();
                    //=========================
                    AddTWR.setLayout(new GridLayout(1, 4, 6, 6));

                    AddTWR.add(NewTask);
                    AddTWR.add(btBranch);
                    AddTWR.add(bLCommit);
                    AddTWR.add(new JPanel());

                    ButtonPanel.add(AddTWR);
                }
                if(tabbedPaneRes.getSelectedIndex()==1)
                {

                    AddTWR.removeAll();
                    //=========================
                    AddTWR.setLayout(new GridLayout(1, 4, 6, 6));

                    AddTWR.add(NewWork);
                    AddTWR.add(btCommit);
                    AddTWR.add(new JPanel());
                    AddTWR.add(new JPanel());


                    ButtonPanel.add(AddTWR);

                }

                content.repaint();
                content.validate();
            }
        });

        //открытие хранилищ
        OpenRep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                    content.removeAll();
                    content.setLayout(new BorderLayout());

                    TablePanel.removeAll();
                    //обновить таблицу проектов
                    ChangeTable(table, 0);
                    //обновить таблицу хранилищ
                    ChangeTable(tableRepo, 111);
                    TablePanel.setLayout(new BorderLayout());
                    TablePanel.add(scrollPaneR, BorderLayout.CENTER);

                    ButtonPanel.removeAll();
                    ButtonPanel.add(ShowInfo);
                    ButtonPanel.add(bLBranch);
                    ButtonPanel.add(new JPanel());

                    TablePanel.repaint();
                    TablePanel.validate();

                    content.add(TablePanel, BorderLayout.CENTER);
                    content.add(ButtonPanel, BorderLayout.NORTH);
                    addInfo = false;
                    addClone = false;
                    add = false;
                    addUser = false;
                    CloneRepo.setText("Панель копирования");
                    CreateRep.setText("Панель добавления");
                    ShowInfo.setText("Показать информацию");
                    NewUser.setText("Новый пользователь");
                    content.repaint();
                    content.validate();
            }
        });


        //TODO Проверка
        //открытие пользователей
        OpenUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                content.removeAll();
                content.setLayout(new BorderLayout());

                TablePanel.removeAll();
                //обновить таблицу проектов
                ChangeTable(table, 0);
                //обновить таблицу хранилищ
                ChangeTable(tableRepo, 111);
                //обновить таблицу пользователей
                //ресурсы
                ChangeTable(tableRes, 2);

                TablePanel.setLayout(new BorderLayout());
                TablePanel.add(TableRes, BorderLayout.CENTER);

                ButtonPanel.removeAll();
                ButtonPanel.add(NewUser);
                ButtonPanel.add(new JPanel());
                ButtonPanel.add(new JPanel());

                TablePanel.repaint();
                TablePanel.validate();

                content.add(TablePanel, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);
                addInfo = false;
                addClone = false;
                add = false;
                addUser = false;
                CloneRepo.setText("Панель копирования");
                CreateRep.setText("Панель добавления");
                ShowInfo.setText("Показать информацию");
                NewUser.setText("Новый пользователь");
                content.repaint();
                content.validate();
            }
        });


        //открытие проектов
        OpenProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                    content.removeAll();
                    content.setLayout(new BorderLayout());

                    TablePanel.removeAll();
                    ChangeTable(table, 0);
                    ChangeTable(tableRepo, 111);

                    ButtonPanel.removeAll();

                    ButtonPanel.add(CreateRep);
                    ButtonPanel.add(CloneRepo);
                    ButtonPanel.add(OpenTWR);


                    TablePanel.setLayout(new BorderLayout());
                    TablePanel.add(scrollPane, BorderLayout.CENTER);
                    TablePanel.repaint();
                    TablePanel.validate();

                    content.add(TablePanel, BorderLayout.CENTER);
                    content.add(ButtonPanel, BorderLayout.NORTH);
                    addInfo = false;
                    addClone = false;
                    add = false;
                    tabRes = false;

                    //восстановление значений
                    NewTask.setText("Новая задача");
                    NewTask.setEnabled(true);
                    NewWork.setText("Новая работа");
                    btCommit.setText("Создание события");
                    btBranch.setText("Создание ветки");
                    NewBranch.setEnabled(true);
                    bLBranch.setText("Список веток");
                    bLCommit.setText("Список событий");


                    addTask = false;
                    addWork = false;
                    addCommit = false;
                    addBranch = false;
                    addnewCommit = false;

                    OpenTWR.setText("Открыть проект");

                    content.add(TablePanel, BorderLayout.CENTER);
                    content.add(ButtonPanel, BorderLayout.NORTH);


                    OpenTWR.setText("Открыть проект");
                    CloneRepo.setText("Панель копирования");
                    CreateRep.setText("Панель добавления");
                    ShowInfo.setText("Показать информацию");
                    content.repaint();
                    content.validate();
            }
        });


        //=============================================
        final JPanel jRepoData =  new JPanel();

        //поля
        final ArrayList<String> pole = new ArrayList<String>();
        //текстовое поле, две даты, два текстовое поле
        pole.add("T");
        pole.add("TX");
        pole.add("T");


        final ArrayList<String> data = new ArrayList<String>();
        data.add("Название хранилища");
        data.add("Путь к хранилищу");
        data.add("Комментарий");

        final int[] rest = new int[2];
        rest[0] = 100;
        rest[1] = 100;
        //===========================================================
        final JPanel jRepoInfo =  new JPanel();

        //===========================================================
        final JPanel jRepoClone =  new JPanel();

        //поля
        final ArrayList<String> pole1 = new ArrayList<String>();
        //текстовое поле, две даты, два текстовое поле
        pole1.add("T");
        pole1.add("TXW");
        pole1.add("CS");
        pole1.add("TX");
        pole1.add("CV");
        pole1.add("T");

        cbStatModel.addElement("Локальное");
        cbStatModel.addElement("Глобальное");

        cbBranchModel.addElement("Master");
        //cbBranchModel.addElement("Первичный контакт");

        final ArrayList<String> data1 = new ArrayList<String>();
        data1.add("Название хранилища");
        data1.add("Ссылка на оригинал");
        data1.add("Вид хранилища");
        data1.add("Путь к копии");
        data1.add("Ветка");
        data1.add("Комментарий");
        //===========================================================
        final int[] rest1 = new int[2];
        rest1[0] = 100;
        rest1[1] = 100;


//Таблица под коммиты
//============================================================
//==========создание таблицы коммитов=================
//создание заголовка таблицы задач
        ArrayList<String> colNCommit = new ArrayList<String>();
        //{""};
        colNCommit.clear();
        colNCommit.add("Название события");
        colNCommit.add("Ответственный");
        colNCommit.add("Дата выполнения");


        //панель под таблицы
        //модель таблицы
        //установить заголовок
        tableCommit.ChColumn(colNCommit);
        //таблица
        final JTable tableCm = new JTable(tableCommit);
        //сортировка
        tableCm.setRowSorter(new TableRowSorter<MyTable3>(tableCommit));
        //запись в панель под таблицы
        CreateTable(tableCm, TablePanelCM);
//========== окончание создания таблицы коммитов=================
//====================================================================


        //панели кнопок
        ButtonPanel.setLayout(new GridLayout(1,3,12,12));
        ButtonPanel.setBorder(EmBorder);



        //панель buttons
        //установить ее расположение на GridLayout
        //6 - кнопок
        //1 - в ряду
        //панель под кнопки "Хранилище" и "Проект"
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 1, 0, 12));
        buttons.setBorder(compound2);

        OpenProject.setEnabled(false);
        OpenProject.setToolTipText("Вернуться к списку проектов");
        OpenRep.setEnabled(false);
        OpenRep.setToolTipText("Посмотреть список хранилищ");
        OpenUser.setEnabled(false);
        OpenUser.setToolTipText("Список пользователей");

        //рамка под кнопки
        JPanel buttonP1 = new JPanel();
        buttonP1.setLayout(new GridLayout(8,1,0,6));
        buttonP1.setBorder(compound3);
        buttonP1.add(OpenProject);
        buttonP1.add(OpenRep);
        buttonP1.add(OpenUser);
        buttons.add(buttonP1);

        ButtonPanel.add(CreateRep);
        ButtonPanel.add(CloneRepo);
        ButtonPanel.add(OpenTWR);


        //создание панелей данных
        Font font = new Font("Arial", Font.PLAIN, 12);
        CreateDataPanel(jRepoData, tableJ, table, 3, pole, data,
                rest, font, 0, null, "AddRepo");
        CreateInfoPanel(jRepoInfo);
        CreateDataPanel(jRepoClone, tableJ, table, 6, pole1, data1,
                rest1, font, 0, null,"CloneRepo");

        //=====================================================================

        //комбобокс под ресурсы
        BClass[] cbRes = new BClass[1];
        cbRes[0] = new BClass(modelRes, 1);

        CreateDataPanel(jDataRes, tableRs, tableRes, 4, pole5, data5,
                rest5, font, 1, cbRes, "");

        //комбобокс под задачи
        BClass[] cbTask = new BClass[2];
        cbTask[0] = new BClass(modelRes, 0);
        cbTask[1] = new BClass(modelTask2, 1);
        //cbTask[2] = new BClass(modelTask1, 1);


        CreateDataPanel(jDataTask, tableTk, tableTask, 6, pole8, data8,
                rest8, font, 3, cbTask, "");

        //комбобокс под ресурсы в работе с ресурсами
        BClass[] cbResWork = new BClass[2];
        cbResWork[0] = new BClass(modelRes, 0);
        cbResWork[1] = new BClass(modelTask1, 0);

        CreateDataPanel(jDataResWork, tableResWork, tableWRes, 7, pole9, data9,
                rest9, font, 4, cbResWork, "");


        //поменять HEAD
        changeHEAD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //проверка что есть хранилище
                if (repo == null) {
                    String msg = "Хранилища не существует";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //проверка на коммит
                if (cbBranch.getItemCount() == 0) {
                    String msg = "Не существует веток в данном хранилище";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //HEAD поменять
                if (repo != null) {

                    String name = listBranch.getSelectedItem().toString();
                    //HEAD поменять на мастера
                    masterHEAD.setText(name);
                    //выбор ветки
                    gitSend.chooseBranch(repo, name);
                }
            }
        });

        //новая ветка
        //======================================================================
        NewBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                int InRow = tableTk.getSelectedRow();

                //проверка что есть выделение
                if(InRow == -1)
                {
                    String msg = "Выберите значение для изменения!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = tableTk.convertRowIndexToModel(InRow);

                //получить выделенную строку
                ArrayList<Object> RowTable = tableTask.getRowAt(row);
                ArrayList<Object> project;
                ArrayList<Object> mData;

                String manager;
                String nameManager = null;
                String email = null;
                try
                {
                    //====================================
                    manager = sql.selectValue(222).get(0)
                            .get(1).toString();
                    sql.nameRes = manager;
                    sql.idManager = (Integer)sql
                            .selectValue(10).get(0).get(0);
                    mData = sql.selectValue(1).get(0);

                    //данные о логине пользователя
                    nameManager = mData.get(3).toString();
                    email = mData.get(3).toString();
                }
                catch (SQLException e1)
                {
                    JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
                }


                if(repo == null)
                {
                    String msg = "Хранилище для данного проекта не существует";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                //получить выделенную строку

                //имя ветки
                String nameBranch = RowTable.get(6).toString();

                if(!nameBranch.equals("-"))
                {
                    String msg = "Задача уже с веткой!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }



                //получение имени для ветки
                String name = RowTable.get(0).toString();
                //сделать большие буквы
                name = capitalizeString(name);
                name = name.replaceAll("\\s","");
                name = name.replaceAll("[^a-zA-Zа-яА-Я0-9 ]", "");

                // run the add-call
                //если создание ветки успешно
                if(gitSend.createBranch(repo, nameManager, email, name))
                {
                    //возвращение мастера
                    masterHEAD.setText("refs/heads/master");
                    //==============================

                    ArrayList<Object> branchRow = new ArrayList<Object>();
                    branchRow.add("refs/heads/"+name);

                    //в раскр список
                    listBranch.addElement("refs/heads/"+name);

                    //=======================================================
                    //добавить строку в таблицу sql

                    try
                    {
                        //добавление значения
                        sql.addValue(branchRow, 5);
                    }
                    catch (SQLException e2)
                    {
                        JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
                        return;
                    }

                    ChangeTable(tableTask, 3);

                    //======================================================

                    JOptionPane.showMessageDialog(new JPanel(), "Ветка создана!");

                }
            }
        });

        //список коммитов
        bLCommit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int InRow = tableTk.getSelectedRow();
                //проверка что есть выделение
                if (InRow == -1) {
                    String msg = "Выберите задачу с веткой!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = tableTk.convertRowIndexToModel(InRow);

                //получить выделенную строку
                ArrayList<Object> Row = tableTask.getRowAt(row);

                //имя ветки
                String nameBranch = Row.get(6).toString();

                if (nameBranch.equals("-")) {
                    String msg = "Выберите задачу с веткой!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }


                //если список не добавлен
                if (!addCommit)
                {

                    if (repo == null)
                    {
                        String msg = "Хранилище для данного проекта не существует";
                        JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                        JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                                "Input Error",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    //стави флаг что добавлен
                    addCommit = true;
                    //удаление текущей таблицы
                    content.remove(tabbedPaneRes);
                    //добавление панели для таблицы
                    content.add(TablePanelCM, BorderLayout.CENTER);
                    bLCommit.setText("Закрыть панель");
                    NewTask.setEnabled(false);
                    btBranch.setEnabled(false);


                    //данные по коммитам в ветке
                    ArrayList<ArrayList<Object>> commitsData
                            = gitSend.listCommit(repo, nameBranch, null);

                    //обновить таблицу коммитов
                    ChangeCommit(tableCommit, commitsData);
                    repo.close();


                }
                else
                {
                    content.remove(TablePanelCM);
                    bLCommit.setText("Список событий");
                    addCommit = false;
                    NewTask.setEnabled(true);
                    btBranch.setEnabled(true);
                    content.add(tabbedPaneRes, BorderLayout.CENTER);

                }

                content.repaint();
                content.validate();

            }
        });



        //список веток
        bLBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                StringBuilder stringBuilder = new StringBuilder();

                int InRow = tableR.getSelectedRow();
                //проверка что есть выделение
                if (InRow == -1) {
                    String msg = "Выберите хранилище!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = tableR.convertRowIndexToModel(InRow);

                String nameRepo = tableRepo.getValueAt(row, 0).toString();
                String pathRepo = tableRepo.getValueAt(row, 1).toString();

                if(nameRepo.contains("(Копия)"))
                {
                    nameRepo = nameRepo.replace("(Копия)", "");
                }
                //установление директории репозитория
                File targetDir = new File(pathRepo+"\\"
                        +nameRepo+"\\.git");

                if(!pathRepo.equals(""))
                    //получение хранилища
                repo = gitSend.setRepo(targetDir);
                else
                repo = null;

                if (repo == null)
                {
                    String msg = "Хранилище не существует";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                stringBuilder.append("Список локальных веток:\n");

                for (String ref : gitSend.getLocalBranches(repo)) {
                    stringBuilder.append("Branch: ").append(ref).append("\n");
                }

                stringBuilder.append("Локальные и глобальные ветки:\n");

                for (String ref : gitSend.getAllBranches(repo)) {
                    stringBuilder.append("Branch: ").append(ref).append("\n");
                }

                JOptionPane.showMessageDialog(new JPanel(), stringBuilder.toString());
            }
        });


        //добавить ветку
        addBranch1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int InRow = tableTk.getSelectedRow();

                //проверка что есть выделение
                if (InRow == -1) {
                    String msg = "Выберите значение для изменения!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = tableTk.convertRowIndexToModel(InRow);

                //получить выделенную строку
                ArrayList<Object> Row = tableTask.getRowAt(row);

                //проверка на коммит
                if (cbBranch.getItemCount() == 0) {
                    String msg = "Не существует веток в данном хранилище";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (repo == null) {
                    String msg = "Хранилище для данного проекта не существует";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }


                //имя ветки
                String nameBranch = Row.get(6).toString();

                if (!nameBranch.equals("-")) {
                    String msg = "Задача уже с веткой!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                ArrayList<Object> branchRow = new ArrayList<Object>();
                branchRow.add(cbBranch.getSelectedItem());

                //=======================================================
                //добавить строку в таблицу sql

                try {
                    //добавление значения
                    sql.addValue(branchRow, 5);
                } catch (SQLException e2) {
                    JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
                    return;
                }

                ChangeTable(tableTask, 3);

                //=======================================================
                JOptionPane.showMessageDialog(new JPanel(), "Ветка добавлена!");

            }
        });


        //добавить существующий commit
        addCommit1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int InRow = tableResWork.getSelectedRow();
                //проверка что есть выделение
                if (InRow == -1) {
                    String msg = "Выберите работу!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }


                int row = tableResWork.convertRowIndexToModel(InRow);

                //получить выделенную строку
                ArrayList<Object> Row = tableWRes.getRowAt(row);


                //коммит
                String commit = Row.get(7).toString();

                if (!commit.equals("Нет события")) {
                    String msg = "Работа уже выполнена";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //проверка на коммит
                if (cbCommit.getItemCount() == 0) {
                    String msg = "Не существует событий в данной ветке с таким пользователем";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //коммит
                ArrayList<Object> commitRow = new ArrayList<Object>();
                //получение коммита из раскрывающегося списка
                commitRow.add(cbCommit.getSelectedItem());


                //=======================================================
                //добавить строку в таблицу sql
                try {
                    //добавление значения
                    sql.addValue(commitRow, 7);
                } catch (SQLException e2) {
                    JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
                    return;
                }
                //изменить таблицу
                ChangeTable(tableWRes, 4);

                //=======================================================
                JOptionPane.showMessageDialog(new JPanel(), "Событие добавлено!");

            }
        });



        //новый commit
        newCommit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int InRow = tableResWork.getSelectedRow();
                //проверка что есть выделение
                if (InRow == -1) {
                    String msg = "Выберите работу!";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int row = tableResWork.convertRowIndexToModel(InRow);

                //получить выделенную строку
                ArrayList<Object> Row = tableWRes.getRowAt(row);

                //коммит
                String commit = Row.get(7).toString();
                //название работы
                String work = Row.get(2).toString();

                if (!commit.equals("Нет события")) {
                    String msg = "Работа уже выполнена";
                    JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //если нет ветки
                if (nameBranch.equals("-")) {
                    String msg = "Выберите работу для задачи \n" +
                            "с созданной веткой!";
                    //JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                    JOptionPane.showMessageDialog(new JPanel(), msg,
                            "Input Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String email = nameManager;

                if (gitSend.createCommit(repo, nameManager, email,
                        nameBranch, work)) {

                    ArrayList<Object> commitRow = new ArrayList<Object>();
                    commitRow.add("Событие: работа сделана: " +
                            "'" + nameManager + "'");


                    //=======================================================
                    //добавить строку в таблицу sql

                    try {
                        //добавление значения
                        sql.addValue(commitRow, 7);
                    } catch (SQLException e2) {
                        JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
                        return;
                    }
                    //изменить таблицу
                    ChangeTable(tableWRes, 4);

                    //=======================================================
                    JOptionPane.showMessageDialog(new JPanel(), "Событие создано!");
                }

            }
        });



        //панель нового пользователя
        NewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                if(!addUser)
                {

                    content.add(jDataRes, BorderLayout.SOUTH);
                    addUser = true;
                    NewUser.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jDataRes);
                    NewUser.setText("Новый пользователь");
                    addUser = false;
                }
                content.repaint();
                content.validate();
            }
        });

        NewTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                content.removeAll();
                content.setLayout(new BorderLayout());
                content.add(tabbedPaneRes, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);

                if(!addTask)
                {
                    content.add(jDataTask, BorderLayout.SOUTH);
                    addTask = true;
                    addBranch = false;
                    btBranch.setText("Создание ветки");
                    NewTask.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jDataTask);
                    NewTask.setText("Новая задача");
                    addTask = false;
                }
                content.repaint();
                content.validate();
            }
        });

        NewWork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                content.removeAll();
                content.setLayout(new BorderLayout());
                content.add(tabbedPaneRes, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);

                if(!addWork)
                {

                    content.add(jDataResWork, BorderLayout.SOUTH);
                    addWork = true;
                    addnewCommit = false;
                    btCommit.setText("Создание события");
                    NewWork.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jDataResWork);
                    NewWork.setText("Новая работа");
                    addWork = false;
                }
                content.repaint();
                content.validate();
            }
        });



        //доббавление панели добавления хранилища
        CreateRep.setToolTipText("Показать панель создания локального хранилища");
        CreateRep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                content.removeAll();
                content.setLayout(new BorderLayout());
                content.add(TablePanel, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);
                addInfo = false;
                addClone = false;
                CloneRepo.setText("Панель копирования");
                ShowInfo.setText("Показать информацию");
                if(!add)
                {

                    content.add(jRepoData, BorderLayout.SOUTH);
                    add = true;
                    CreateRep.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jRepoData);
                    CreateRep.setText("Панель добавления");
                    add = false;
                }
                content.repaint();
                content.validate();
            }
        });

        ShowInfo.setToolTipText("Показать общую информацию о хранилищах");
        ShowInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                content.removeAll();
                content.setLayout(new BorderLayout());
                content.add(TablePanel, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);
                add = false;
                addClone = false;
                CloneRepo.setText("Панель копирования");
                CreateRep.setText("Панель добавления");
                if(!addInfo)
                {

                    if(repoNew)
                    {
                        numGlobal=0;
                        numCommon=0;
                        numLocal=0;
                        int max = tableRepo.getRowCount();
                        for(int i = 0;i<max;i++)
                        {
                             String typeRepo = tableRepo.getValueAt(i, 3)
                                     .toString();
                             if(typeRepo.equals("Глобальное"))
                                 numGlobal++;
                             else numLocal++;
                             numCommon++;
                        }


                        labelsV[0].setText(Integer.toString(numCommon));
                        labelsV[1].setText(Integer.toString(numLocal));
                        labelsV[2].setText(Integer.toString(numGlobal));
                        repoNew = false;
                    }

                    content.add(jRepoInfo, BorderLayout.SOUTH);
                    addInfo = true;
                    ShowInfo.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jRepoInfo);
                    ShowInfo.setText("Показать информацию");
                    addInfo = false;
                }
                content.repaint();
                content.validate();
            }
        });


        CloneRepo.setToolTipText("Открыть панель копирования удаленного хранилища в локальное");
        CloneRepo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                content.removeAll();
                content.setLayout(new BorderLayout());
                content.add(TablePanel, BorderLayout.CENTER);
                content.add(ButtonPanel, BorderLayout.NORTH);
                add = false;
                addInfo = false;
                ShowInfo.setText("Показать информацию");
                CreateRep.setText("Панель добавления");
                if(!addClone)
                {

                    content.add(jRepoClone, BorderLayout.SOUTH);
                    addClone = true;
                    CloneRepo.setText("Закрыть панель");
                }
                else
                {
                    content.remove(jRepoClone);
                    CloneRepo.setText("Панель копирования");
                    addClone = false;
                }
                content.repaint();
                content.validate();
            }
        });

        //вспомогательные панели для размещения
        // панели для ввода логина и пароля
        JPanel helpPass1 = new JPanel();
        helpPass1.setLayout(new GridLayout(3,1));
        //==========================
        helpPass1.add(new JPanel());
        helpPass1.add(loginPass);
        helpPass1.add(new JPanel());


        //в начале панель сделать под логин и пароль
        content.removeAll();
        content.setLayout(new GridLayout(1,3));

        content.add(new JPanel());
        content.add(helpPass1);
        content.add(new JPanel());
        //добавить на форму


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(content, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.EAST);


        //поменять размер формы
        setPreferredSize(new Dimension(960, 600));
        //по ширине
        pack();
        //в центр экрана
        setLocationRelativeTo(null);
        //установить видимой
        setVisible(true);
    }



    //ФУНКЦИИ

    //создание панели данных
    /**
     * Create comlpex data panel
     * @param jDataMain - panel on which panel is creating
     * @param tbl - table with which panel is working
     * @param tableModel - model of table with which panel is working
     * @param col - number of parts
     * @param pole - different value type of parts
     * @param data - data of parts
     * @param rest - limits of text values
     * @param font - font
     * @param sel  - number of SQL table
     * @param model - class of BClass
     * @param statButton - statButton
     */
    private void CreateDataPanel(JPanel jDataMain, final JTable tbl,
                                 final MyTable3 tableModel, final int col,
                                 final ArrayList<String> pole,
                                 ArrayList<String> data, final int[] rest,
                                 Font font, final int sel, final BClass[] model,
                                 final String statButton)
    {

        int numCB = 0; //номер комбобокса

        //создание рамки
        Border EmBorder = BorderFactory.createEmptyBorder(8,8,8,8);
        Border LowBorder = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(LowBorder, EmBorder);

        //панель для ввода данных
        jDataMain.setLayout(new BorderLayout());  //установка расположения

        //создание панели под данные
        JPanel jData =  new JPanel();
        int row;
        if(col%2==0)
        row = col/2;
        else
        row = (col/2)+1;

        jData.setLayout(new GridLayout(row,2,0,0));  //установка расположения
        jData.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));  //рамка

        //создание необходимых полей
        JPanel[] panels = new JPanel[col];


        //label
        JLabel[] labels =  new JLabel[col];
        for(int i = 0; i < col; i++)
        {
            labels[i] = new JLabel(data.get(i));
            labels[i].setFont(font);
        }

        //кнопки для добавления, удаления, изменения
        JPanel jInt =  new JPanel();
        jInt.setLayout(new GridLayout(1,6,10,10));
        jInt.setBorder(new EmptyBorder(8,8,8,8));

        final JButton JBData = new JButton("");
        final JButton JBChange = new JButton("");
        final JButton JBDelete = new JButton("");

        if(statButton.equals("AddRepo"))
        {
            JBData.setText("Добавить локальное хранилище");
            JBData.setToolTipText("Добавить хранилище по локальной ссылке");
            jInt.add(JBData);
            jInt.add(new JPanel());

        }
        else
        if(statButton.equals("CloneRepo"))
        {
            JBData.setText("Копировать");
            JBData.setToolTipText("Клонировать хранилище по ссылке на локальный компьютер");
            jInt.add(JBData);
            jInt.add(new JPanel());
        }
        else
        {
            JBData.setText("Добавить");
            JBChange.setText("Изменить");
            JBDelete.setText("Удалить");
            jInt.add(JBData);
            jInt.add(JBChange);
            jInt.add(JBDelete);
            jInt.add(new JPanel());
            jInt.add(new JPanel());
        }

        //добавление на панель кнопок
        jInt.add(new JPanel());


        //список компонентов
        final ArrayList<Object> comp = new ArrayList<Object>();

        int textCol = 0;
        for(int i = 0; i < col; i++)
        {
            panels[i] = new JPanel();
            panels[i].setLayout(new GridLayout(1,2,0,0));
            panels[i].setBorder(compound);
            panels[i].add(labels[i]);

            //в зависимости от поданного значения выбираются поля для ввода
            if(pole.get(i).equals("T"))
            {

                //текстовое поле
                final JFormattedTextField textField = new JFormattedTextField();
                //ограничение
                //textField.setDocument(new JTextFieldLimit(rest[textCol]));
                //кнопка
                JButton button = new JButton("...");
                button.setPreferredSize(new Dimension(25, 15));

                //получить конкретное ограничение
                //int res = rest[textCol];
                //текстовое поле
                final JEditorPane textArea = new JEditorPane();
                //textArea.setDocument(new JTextFieldLimit(res));
                //прокрутка
                final JScrollPane editorScrollPane = new JScrollPane(textArea);
                //всегда прокрутка
                editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                //размер
                editorScrollPane.setPreferredSize(new Dimension(250, 145));
                editorScrollPane.setMinimumSize(new Dimension(10, 10));

                button.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        //получить старый текст
                        textArea.setText(textField.getText());
                        //показать диалог
                        int okCxl = JOptionPane.showConfirmDialog(null,
                                editorScrollPane,
                                "Введите значение",
                                JOptionPane.OK_CANCEL_OPTION);

                        if (okCxl == JOptionPane.OK_OPTION)
                        {
                            textField.setText(textArea.getText());
                        }
                    }
                });
                //кнопка внутри поля
                ComponentBorder cb = new ComponentBorder(button);
                cb.setAlignment(0.3f);
                cb.install(textField);
                comp.add(textField);
                //добавить текстовое поле на панель
                panels[i].add(textField);
                textCol++;
            }

            //в зависимости от поданного значения выбираются поля для ввода
            if(pole.get(i).equals("TX"))
            {

                //текстовое поле
                final JFormattedTextField textField = new JFormattedTextField();
                textField.setEditable(false);
                //кнопка
                JButton button = new JButton("...");
                button.setPreferredSize(new Dimension(25, 15));

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        fileDir = "";
                        chooser.setDialogTitle("Select target directory");
                        int ret = chooser.showDialog(null, "Открыть папку");

                        if (ret == JFileChooser.APPROVE_OPTION)
                        {
                            fileDir = chooser.getSelectedFile().getAbsolutePath();
                            textField.setText(fileDir);
                            textField.setCaretPosition(0);
                        }
                    }
                });


                //кнопка внутри поля
                ComponentBorder cb = new ComponentBorder(button);
                cb.setAlignment(0.3f);
                cb.install(textField);
                comp.add(textField);
                //добавить текстовое поле на панель
                panels[i].add(textField);
                textCol++;
            }
            //в зависимости от поданного значения выбираются поля для ввода
            if(pole.get(i).equals("TXW"))
            {

                //текстовое поле
                final JFormattedTextField textField = new JFormattedTextField();
                //textField.setEditable(false);
                //кнопка
                JButton button = new JButton("...");
                button.setPreferredSize(new Dimension(25, 15));

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        fileWeb = "";
                        chooser.setDialogTitle("Select target directory");
                        int ret = chooser.showDialog(null, "Открыть хранилище");

                        if (ret == JFileChooser.APPROVE_OPTION)
                        {
                            String text = chooser.getSelectedFile().getAbsolutePath();
                            textField.setText(text);
                            textField.setCaretPosition(0);
                        }
                        fileWeb = textField.getText();
                    }
                });


                //кнопка внутри поля
                ComponentBorder cb = new ComponentBorder(button);
                cb.setAlignment(0.3f);
                cb.install(textField);
                comp.add(textField);
                //добавить текстовое поле на панель
                panels[i].add(textField);
                textCol++;
            }

            if(pole.get(i).equals("N"))
            {
                //спиннер
                JSpinner spinner = new JSpinner();
               /* spinner; */
                comp.add(spinner);
                panels[i].add(spinner);
            }
            if(pole.get(i).equals("D"))
            {
                //дата
                JDateChooser dateChooser = new JDateChooser();
                comp.add(dateChooser);
                panels[i].add(dateChooser);
            }
            if(pole.get(i).equals("C"))
            {
                //комбобокс с добавлением удалением
                //?
                JComboBox<String> comboBox = new JComboBox<String>(model[numCB].CB);
                comboBox.setPreferredSize(new Dimension(100, 15));
                comp.add(comboBox);
                panels[i].add(comboBox);
                numCB++;
            }
            if(pole.get(i)=="CS")
            {
                //статусы
                JComboBox<String> comboBox = new JComboBox<String>(cbStatModel);
                comboBox.setPreferredSize(new Dimension(100, 15));
                comp.add(comboBox);
                panels[i].add(comboBox);
            }
            if(pole.get(i).equals("CR"))
            {
                //статусы
                JComboBox<String> comboBox = new JComboBox<String>(cbSModel);
                comboBox.setPreferredSize(new Dimension(100, 15));
                comp.add(comboBox);
                panels[i].add(comboBox);
            }
            if(pole.get(i).equals("CK"))
            {
                //статусы
                JComboBox<String> comboBox = new JComboBox<String>(cbKModel);
                comboBox.setPreferredSize(new Dimension(100, 15));
                comp.add(comboBox);
                panels[i].add(comboBox);
            }
            if(pole.get(i).equals("CV"))
            {
                //статусы
                final JComboBox<String> comboBox = new JComboBox<String>(cbBranchModel);
                comboBox.setEnabled(false);
                comboBox.setPreferredSize(new Dimension(100, 15));
                comp.add(comboBox);

                //checkBox.
                checkBox.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {
                        comboBox.setEnabled(checkBox.isSelected());
                    }
                });
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(comboBox, BorderLayout.EAST);
                panel.add(checkBox, BorderLayout.CENTER);
                panels[i].add(panel);
            }
            //jData.add(panels[i]);
        }

        //добавление сверху вниз по двум столбцам
        for(int j = 0;j<row;j++)
        {
            jData.add(panels[j]);
            if(j+row<col)
                jData.add(panels[j+row]);
        }

        //обработчики кнопок
        //обработчики событий
        JBData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //добавление значения в table
                //если добавление

                if(statButton.equals("AddRepo"))
                AddValueRepos(table, tbl, pole, comp, true);
                else
                if(statButton.equals("CloneRepo"))
                {
                    typeRepo = cbStatModel.getSelectedItem().toString();
                    CloneValueRepos(table, tbl, pole, comp);
                }
                else
                {
                    AddValue(pole, sel, comp, model);
                }
            }
        });

        //добавить существующее
        JBChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //добавление значения в table
                //если добавление
                ChangeValue(tableModel, tbl, pole, sel, comp, model);
            }
        });

        JBDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //удаления значения из table по tbl
                //удаление по запросу sql по sel
                DelValue(tableModel, tbl, sel, model);
            }
        });

        //добавление панели на нечетное количество
        if(col%2!=0)
        {
            JPanel panel = new JPanel();
            panel.setBorder(compound);
            jData.add(panel);
        }

        //обработчик двойного щелчка по таблице
        tbl.addMouseListener(new MouseInputAdapter()
        {
            public void mousePressed(MouseEvent me)
            {


                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                int InRow = table.rowAtPoint(p);
                if(InRow!=-1)
                {
                    //6 - для таблицы риск
                    getIDRow(tbl, tableModel, sel);

                    int row = table.convertRowIndexToModel(InRow);
                    //получить выделенную строку
                    ArrayList<Object> Row =
                            new ArrayList<Object>(tableModel.getRowAt(row));

                    if (me.getClickCount() == 2)
                    {
                        int amount = col;
                        if(sel==1) amount--;
                        for(int i = 0; i < amount; i++)
                        {
                            if(pole.get(i).equals("T"))
                            {
                                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                                textField.setText(Row.get(i).toString());
                            }
                            if(pole.get(i).equals("N"))
                            {
                                JSpinner spinner = (JSpinner)comp.get(i);
                                spinner.setValue((int)Float.parseFloat(Row.get(i).toString()));
                            }
                            if(pole.get(i).equals("D"))
                            {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = null;
                                try
                                {
                                    date = simpleDateFormat.parse(Row.get(i).toString());
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                                JDateChooser dateChooser = (JDateChooser)comp.get(i);
                                dateChooser.setDate(date);
                            }
                            if(pole.get(i).equals("C"))
                            {
                                JComboBox comboBox = (JComboBox)comp.get(i);
                                comboBox.setSelectedItem(Row.get(i));
                            }
                            if(pole.get(i).equals("CS")|| pole.get(i).equals("CR")
                                    || pole.get(i).equals("CK"))
                            {
                                JComboBox comboBox = (JComboBox)comp.get(i);
                                comboBox.setSelectedItem(Row.get(i));
                            }
                        }
                    }
                }
            }
        });

        //добавление на главную панель
        jDataMain.add(jData, BorderLayout.CENTER);
        jDataMain.add(jInt, BorderLayout.SOUTH);
    }


    //создание панели данных

    /**
     * Create infoPanel
     * @param jInfoMain - main panel for InfoPanel
     */
    private void CreateInfoPanel(JPanel jInfoMain)
    {
        //создание рамки
        Border EmBorder = BorderFactory.createEmptyBorder(6,12,6,12);
        Border LowBorder = BorderFactory.createLoweredBevelBorder();
        Border compound = BorderFactory.createCompoundBorder(LowBorder, EmBorder);

        //панель для ввода данных
        jInfoMain.setLayout(new BorderLayout());  //установка расположения


        //создание панели под данные
        JPanel jInfo =  new JPanel();
        jInfo.setLayout(new GridLayout(3,1,0,0));  //установка расположения
        jInfo.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));  //рамка

        //создание необходимых полей
        JPanel[] panels = new JPanel[3];

        //label
        JLabel[] labels =  new JLabel[3];



        labels[0] = new JLabel("Количество хранилищ");
        labelsV[0] = new JLabel(Integer.toString(numCommon),
                    JLabel.CENTER);
        labelsV[0].setBorder(LowBorder);


        labels[1] = new JLabel("Локальные хранилища");
        labelsV[1] = new JLabel(Integer.toString(numLocal),
                JLabel.CENTER);
        labelsV[1].setBorder(LowBorder);

        labels[2] = new JLabel("Глобальные хранилища");
        labelsV[2] = new JLabel(Integer.toString(numGlobal),
                JLabel.CENTER);
        labelsV[2].setBorder(LowBorder);


        for(int i = 0; i < 3; i++)
        {
            panels[i] = new JPanel();
            panels[i].setLayout(new GridLayout(1,2,0,0));
            panels[i].setBorder(compound);
            panels[i].add(labels[i]);
            panels[i].add(labelsV[i]);
            jInfo.add(panels[i]);
        }


        //добавление на главную панель
        jInfoMain.add(jInfo);

    }

    //обработчик добавления в таблицу
    private void AddValueRepos(MyTable3 table, JTable tableRow, ArrayList<String> data,
                               ArrayList<Object> comp, boolean notexist)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //добавление в таблицу строки
        ArrayList<Object> Row = new ArrayList<Object>();

        int InRow = tableRow.getSelectedRow();

        //проверка что есть выделение
        if(InRow == -1)
        {
            String msg = "Выберите значение для изменения!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                    "Input Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int row = tableRow.convertRowIndexToModel(InRow);

        //получить выделенную строку
        ArrayList<Object> RowTable = table.getRowAt(row);

        if(!RowTable.get(6).equals("-"))
        {
            Object[] options = {"Да","Нет"};
            int reply = JOptionPane.showOptionDialog(null,
                    "У этого проекта уже есть хранилище. " +
                            "Изменить его?",
                    "Проверка", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[1]);

            if (reply == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        //проверка на пустые поля и ввод
        for(int i = 0; i < comp.size(); i++)
        {
            //если текстовое поле
            if(data.get(i).equals("T"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Текстовое поле не заполнено");
                    return;
                }
                //если не пустое то добавляется в строку
                else Row.add(textField.getText());

            }
            if(data.get(i).equals("TX"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Путь не выбран");
                    return;
                }
                //если не пустое то добавляется в строку
                else
                {
                    Row.add(textField.getText());
                    Row.add("Локальное");
                }

            }
            if(data.get(i).equals("D"))
            {
                JDateChooser dateChooser = (JDateChooser)comp.get(i);
                if(dateChooser.getDate()==null)
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Дата не введена");
                    return;
                }
                //если не равна нулю
                else
                {
                    Date dDate = dateChooser.getDate();
                    Row.add(dateFormat.format(dDate));
                }
            }
            //если это число
            if(data.get(i).equals("N"))
            {
                JSpinner spinner = (JSpinner)comp.get(i);
                Row.add(spinner.getValue());
            }
            //если это combobox
            if(data.get(i).equals("C"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CS"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CV"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CK"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
        }

        File targetDir = new File(fileDir+"/"+Row.get(0));


        //если хранилище не существует
        if(notexist)
        {
            String selectName = "";
            String selectEmail = "";
            try
            {
                //====================================
                sql.nameRes = sql.selectValue(222)
                        .get(0).get(1).toString();
                sql.idManager = (Integer)sql
                        .selectValue(10).get(0).get(0);
                ArrayList<Object> mData = sql.selectValue(1).get(0);

                //данные о логине пользователя
                selectName = mData.get(3).toString();
                selectEmail = mData.get(3).toString();
            }
            catch (SQLException e1)
            {
                JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
            }


            if(gitSend.createRepository(targetDir, selectName,
                    selectEmail))
            {
                String msg = "Хранилище создано!";
                JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
                JOptionPane.showMessageDialog(new JPanel(), msgLabel, "Input Accepted",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                return;
            }

        }

        //проверка, есть ли хранилище
        if(!targetDir.exists())
        {
            String msg = "Такого хранилища нет!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel, "Input denied",
            JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        //==================================================================
        ArrayList<Object> RowStr = new ArrayList<Object>();
        /*
        ("Название хранилища");
        ("Путь к хранилищу");
        ("Комментарий");
        */
        RowStr.add(Row.get(0));
        RowStr.add(false);
        RowStr.add(Row.get(1));
        RowStr.add(Row.get(2));
        RowStr.add(Row.get(3));

        boolean add;

        //=======================================================
        //добавить строку в таблицу sql

        try
        {
            //добавление значения
            add = sql.addValue(RowStr, 0);
        }
        catch (SQLException e2)
        {
            JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
            return;
        }
        if(add)
        {
            table.addRow(Row);
            ChangeTable(table, 0);
            repoNew = true;
        }
        //=======================================================

    }

    //обработчик добавления в таблицу
    private void CloneValueRepos(MyTable3 table, JTable tableRow,
                                 ArrayList<String> data,
                                 ArrayList<Object> comp)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //добавление в таблицу строки
        ArrayList<Object> Row = new ArrayList<Object>();

        int InRow = tableRow.getSelectedRow();

        //проверка что есть выделение
        if(InRow == -1)
        {
            String msg = "Выберите значение для изменения!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                    "Input Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int row = tableRow.convertRowIndexToModel(InRow);

        //получить выделенную строку
        ArrayList<Object> RowTable = table.getRowAt(row);

        if(!RowTable.get(6).equals("-"))
        {
            Object[] options = {"Да","Нет"};
            int reply = JOptionPane.showOptionDialog(null,
                    "У этого проекта уже есть хранилище. " +
                            "Изменить его?",
                    "Проверка", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[1]);

            if (reply == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        //проверка на пустые поля и ввод
        for(int i = 0; i < comp.size(); i++)
        {
            //если текстовое поле
            if(data.get(i).equals("T"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Текстовое поле не заполнено");
                    return;
                }
                //если не пустое то добавляется в строку
                else Row.add(textField.getText());

            }
            if(data.get(i).equals("TX"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Путь не выбран");
                    return;
                }
                //если не пустое то добавляется в строку
                else Row.add(textField.getText());
            }
            if(data.get(i).equals("TXW"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Хранилище не выбрано");
                    return;
                }
                //если не пустое то добавляется в строку
                else
                {
                    Row.add(textField.getText());
                    fileWeb = textField.getText();
                }
            }
            if(data.get(i).equals("D"))
            {
                JDateChooser dateChooser = (JDateChooser)comp.get(i);
                if(dateChooser.getDate()==null)
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Дата не введена");
                    return;
                }
                //если не равна нулю
                else
                {
                    Date dDate = dateChooser.getDate();
                    Row.add(dateFormat.format(dDate));
                }
            }
            //если это число
            if(data.get(i).equals("N"))
            {
                JSpinner spinner = (JSpinner)comp.get(i);
                Row.add(spinner.getValue());
            }
            //если это combobox
            if(data.get(i).equals("C"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CS"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CV"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
        }
//=======================================================
//=======================================================


        String sourceDir;
        boolean global;


        //директория источника
        if(typeRepo.equals("Локальное"))
        {
            sourceDir = "file://"+fileWeb;
            global = false;
        }
        else
        {
            sourceDir = fileWeb;
            if(!sourceDir.startsWith("https://"))
            {
                JOptionPane.showMessageDialog(new JPanel(),
                        "Используйте HTTPS URL для клонирования удаленного хранилища!");
                return;

            }
            global = true;

        }

        //целевая директория
        File targetDir = new File(fileDir+"/"+Row.get(0));


            //если директория существует, то удалить
            if(targetDir.exists())
            {

                File reps = new File(fileDir+"/"+Row.get(0)+"/.git");
                if (!gitSend.isRepository(reps))
                {
                    JOptionPane.showMessageDialog(new JPanel(),
                            "Нельзя перезаписывать существующую директорию, " +
                                    "если она не хранилище!");
                    return;
                }

                Object[] options = {"Да","Нет"};
                int reply = JOptionPane.showOptionDialog(null,
                        "Хранилище "+ targetDir.getAbsoluteFile()+ " уже существует. " +
                                "Удалить и записать на ее место?",
                        "Проверка", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null, options, options[1]);

                if (reply == JOptionPane.NO_OPTION)
                {
                    return;
                }
                if(deleteDirectory(targetDir))
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Директория удалена!");
                }
            }

           //преобразование для добавления в таблицу
           //fileWeb
           ArrayList<Object> RowStr = new ArrayList<Object>();
           //для добавления в таблицу
           RowStr.add(Row.get(0)+"(Копия)");
           RowStr.add(targetDir.getParentFile());
           RowStr.add(Row.get(2));  //тип хранилища
           RowStr.add(Row.get(5));  //комментарий


        boolean needBranch = checkBox.isSelected();

        //клонирование хранилища
        if(gitSend.cloneRepository(needBranch, sourceDir,
                targetDir, global))
        {
            String msg = "Хранилище создано!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel, "Input Accepted",
                    JOptionPane.INFORMATION_MESSAGE);

            //==================================================================
            ArrayList<Object> addStr = new ArrayList<Object>();  //для добавления в БД
            /*
            ("Название хранилища");
            ("Путь к хранилищу");
            ("Комментарий");
            */
            addStr.add(RowStr.get(0));
            addStr.add(true);
            addStr.add(RowStr.get(1));
            addStr.add(RowStr.get(2));
            addStr.add(RowStr.get(3));

            //=======================================================
            //добавить строку в таблицу sql
            boolean add;
            try
            {
                //добавление значения
                add = sql.addValue(addStr, 0);
            }
            catch (SQLException e2)
            {
                JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
                return;
            }

            //если добавлено в БД, то обновить таблицу на форме
            if(add)
            {
                table.addRow(RowStr);
                ChangeTable(table, 0);
                repoNew = true;
            }
            //=======================================================
        }

    }


    //обработчик добавления в таблицу
    private void AddValue(ArrayList<String> data,
                          int sel, ArrayList<Object> comp,
                          final BClass[] model)
    {


        int numDate = 0;
        Date date1 = new Date(), date2 = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //добавление в таблицу строки
        ArrayList<Object> Row = new ArrayList<Object>();
        String Inheritant = "";


        //проверка на пустые поля и ввод
        for(int i = 0; i < comp.size(); i++)
        {
            //если текстовое поле
            if(data.get(i).equals("T"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Текстовое поле не заполнено");
                    return;
                }
                //если не пустое то добавляется в строку
                else Row.add(textField.getText());

            }
            //дата
            if(data.get(i).equals("D"))
            {
                JDateChooser dateChooser = (JDateChooser)comp.get(i);
                if(dateChooser.getDate()==null)
                {
                    JOptionPane.showMessageDialog(new JPanel(), "Дата не введена");
                    return;
                }
                //если не равна нулю
                else
                {
                    Date dDate = dateChooser.getDate();
                    if(numDate==0)
                    date1 = dateChooser.getDate();
                    if(numDate==1)
                    date2 = dateChooser.getDate();
                    numDate++;
                    Row.add(dateFormat.format(dDate));
                }
            }
            //если это число
            if(data.get(i).equals("N"))
            {
                JSpinner spinner = (JSpinner)comp.get(i);
                Row.add(spinner.getValue());
            }
            //если это combobox
            if(data.get(i).equals("C"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Inheritant = comboBox.getSelectedItem().toString();
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CS"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CR"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
            if(data.get(i).equals("CK"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Row.add(comboBox.getSelectedItem().toString());
            }
        }


        ArrayList<ArrayList<Object>> RowStr;
        boolean add;
//=======================================================
        //добавить строку в таблицу sql
        try
        {

            String whichDate = dateExecution(date1, sel, Inheritant);
            if(whichDate!=null)
            {
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(date1);
                JOptionPane.showMessageDialog(new JPanel(),
                        "Дата: '"+date+"' введена вне " +
                                "границ "+whichDate+"!");
                return;
            }

            whichDate = dateExecution(date2, sel, Inheritant);
            if(whichDate!=null)
            {
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(date2);
                JOptionPane.showMessageDialog(new JPanel(),
                        "Дата: '"+date+"' введена вне " +
                                "границ "+whichDate+"!");
                return;
            }

            /*
            if(!dateInheritant(dateInheritant, Inheritant,sel))
            {
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(dateInheritant);
                String dateTask = getDateTask(Inheritant);
                JOptionPane.showMessageDialog(new JPanel(),
                        "Последующая задача: "+dateTask+"\nначинается " +
                                "раньше, чем заканчивается текущая:\n"
                                + date);
                return;
            }
            */

            //проверка уникальности
            if(uniqueType(sel, Row, false)==null)
                return;

            Row.add(null);
            //добавление значения
            add = sql.addValue(Row, sel);

            //если модель combobox не равна 0
            if(model!=null)
                for (BClass aModel : model)
                {
                    if (aModel.stat != 0) {
                        //получение вводимого значения
                        String name = sql.getValue(sel);
                        int size = aModel.CB.getSize();
                        if (size > 0 && sel == 3)
                            aModel.CB.insertElementAt(name, size - 1);
                        else
                            aModel.CB.addElement(name);
                    }
                }
        }
        catch (SQLException e2)
        {
            System.out.println(e2.getMessage());
            return;
        }

//=======================================================
        if(add)
        updateTable(sel);

    }

    //обработчик удаления из таблицы
    private void DelValue(MyTable3 tableModel, JTable table, int sel,
                          final BClass[] model)
    {
        int InRow = table.getSelectedRow();


        if(InRow == -1)
        {
            String msg = "Выберите значение для удаления!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel, "Input Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object[] options = {"Да",
                "Нет"};

        int reply = JOptionPane.showOptionDialog(null,
                "Вы хотите удалить выбранное значение?",
                "Проверка", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[1]);

        if (reply == JOptionPane.NO_OPTION)
        {
            return;
        }


        int row = table.convertRowIndexToModel(InRow);
        //получить выделенную строку
        ArrayList<Object> Row = tableModel.getRowAt(row);

        if(model!=null)
            for (BClass aModel : model)
            {
                if (aModel.stat != 0) {
                    aModel.CB.removeElement(Row.get(0));
                }
            }
//=======================================================

        try
        {
            sql.delValue(sel);
        }
        catch (SQLException e2)
        {
            System.out.println(e2.getMessage());
        }

//=======================================================
        tableModel.delRow(InRow);
        updateTable(sel);

    }

    //обработчик изменения таблицы
    private void ChangeValue(MyTable3 tableModel, JTable table,
                             ArrayList<String> data,
                             int sel, ArrayList<Object> comp,
                             final BClass[] model)
    {

        String Inheritant = "";
        int InRow = table.getSelectedRow();

        int numDate = 0;
        Date date1 = new Date(), date2 = new Date();

        //проверка что есть выделение
        if(InRow == -1)
        {
            String msg = "Выберите значение для изменения!";
            JLabel msgLabel = new JLabel(msg, JLabel.CENTER);
            JOptionPane.showMessageDialog(new JPanel(), msgLabel,
                    "Input Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int row = table.convertRowIndexToModel(InRow);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //добавление в таблицу строки
        ArrayList<Object> NewRow = new ArrayList<Object>();

        //проверка на пустые поля и ввод
        for(int i = 0; i < comp.size(); i++)
        {
            //если текстовое поле
            if(data.get(i).equals("T"))
            {
                //преобразование компонента в JFormattedTextField
                JFormattedTextField textField = (JFormattedTextField)comp.get(i);
                if(textField.getText().isEmpty())
                {
                    JOptionPane.showMessageDialog(new JPanel(),
                            "Текстовое поле не заполнено");
                    return;
                }
                //если не пустое то добавляется в строку
                else NewRow.add(textField.getText());

            }
            //если дата
            if(data.get(i).equals("D"))
            {
                JDateChooser dateChooser = (JDateChooser)comp.get(i);
                if(dateChooser.getDate()==null)
                {
                    JOptionPane.showMessageDialog(new JPanel(),
                            "Дата не введена");
                    return;
                }
                //если не равна нулю
                else
                {
                    Date dDate = dateChooser.getDate();
                    if(numDate==0)
                    date1 = dateChooser.getDate();
                    if(numDate==1)
                    date2 = dateChooser.getDate();
                    numDate++;
                    NewRow.add(dateFormat.format(dDate));
                }
            }
            //если это число
            if(data.get(i).equals("N"))
            {
                JSpinner spinner = (JSpinner)comp.get(i);
                NewRow.add(spinner.getValue());
            }
            //если это combobox
            if(data.get(i).equals("C"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                Inheritant = comboBox.getSelectedItem().toString();
                NewRow.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CS"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                NewRow.add(comboBox.getSelectedItem().toString());
            }
            //если это combobox status
            if(data.get(i).equals("CR"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                NewRow.add(comboBox.getSelectedItem().toString());
            }
            if(data.get(i).equals("CK"))
            {
                JComboBox comboBox = (JComboBox)comp.get(i);
                NewRow.add(comboBox.getSelectedItem().toString());
            }
        }

        String whichDate = dateExecution(date1, sel, Inheritant);

        if(whichDate!=null)
        {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(date1);
            JOptionPane.showMessageDialog(new JPanel(),
                    "Дата: '"+date+"' введена вне границ "+whichDate+"!");
            return;
        }

        whichDate = dateExecution(date2, sel, Inheritant);

        if(whichDate!=null)
        {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(date2);
            JOptionPane.showMessageDialog(new JPanel(),
                    "Дата: '"+date+"' введена вне границ "+whichDate+"!");
            return;
        }

        /*
        if(!dateInheritant(dateInheritant, Inheritant,sel))
        {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(dateInheritant);
            String dateTask = getDateTask(Inheritant);
            JOptionPane.showMessageDialog(new JPanel(),
                    "Последующая задача: "+dateTask+"\nначинается " +
                            "раньше, чем заканчивается текущая:\n"
                            +"'"+date+"'");
            return;
        }
        */
        //проверка уникальности
        if(uniqueType(sel, NewRow, true)==null)
            return;


        //получить выделенную строку
        ArrayList<Object> OldRow = tableModel.getRowAt(row);
        OldRow.add(null);

        if(model!=null)
            for (BClass aModel : model)
            {
                if (aModel.stat != 0)
                {
                    aModel.CB.removeElement(OldRow.get(0));
                    int size = aModel.CB.getSize();
                    if (size > 0 && sel == 3)
                        aModel.CB.insertElementAt(NewRow.get(0), size - 1);
                    else
                        aModel.CB.addElement(NewRow.get(0));
                }
            }
//=======================================================

        try
        {
            //0 - для таблицы проекта
            sql.changeValue(NewRow, sel);

        }
        catch (SQLException e2)
        {
            System.out.println(e2.getMessage());
        }

//=======================================================
        //tableModel.delRow(row);
        //tableModel.addRow(NewRow);
        //обновить таблицы
        updateTable(sel);
    }

    //удаление папки
    //delete file - DANGEROUS
    public static boolean deleteDirectory(File directory)
    {
        if(directory.exists())
        {
            File[] files = directory.listFiles();
            if(null!=files){
                for (File file : files)
                {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else
                    {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    //обновление таблицы
    private  void ChangeTable(MyTable3 table, int sel)
    {

        //количество строк в таблице
        int RowCount = table.getRowCount();
        int j = RowCount-1;

        //удалить старую таблицу
        while (j>=0)
        {
            table.delRow(j);  //удаление строки таблицы
            j--;
        }

        //новый массив строк
        ArrayList<ArrayList<Object>> RowStr;
        //инициализация
        RowStr = new ArrayList<ArrayList<Object>>();

        //получить значения
        try
        {
            RowStr = sql.selectValue(sel); //получение sql запроса
        }
        catch (SQLException e2)
        {
            JOptionPane.showMessageDialog(new JPanel(), e2.getMessage());
        }

        int i = 0;
        int Size = RowStr.size();  //количество строк
        ArrayList<Object> Row;  //одна строка

        //добавление в таблицу
        while (i<Size)
        {
            Row = RowStr.get(i);   //получение строки из массива
            table.addRow(Row);  //добавление в таблицу
            i++;
        }
    }

    //обновление таблицы
    private  void ChangeCommit(MyTable3 table, ArrayList<ArrayList<Object>> RowStr)
    {

        //количество строк в таблице
        int RowCount = table.getRowCount();
        int j = RowCount-1;

        //удалить старую таблицу
        while (j>=0)
        {
            table.delRow(j);  //удаление строки таблицы
            j--;
        }

        int i = 0;
        int Size = RowStr.size();  //количество строк
        ArrayList<Object> Row;  //одна строка

        //добавление в таблицу
        while (i<Size)
        {
            Row = RowStr.get(i);   //получение строки из массива
            table.addRow(Row);  //добавление в таблицу
            i++;
        }
    }


    //получение значения идентификатора выделенной строки таблицы
    private void getIDRow(JTable table, MyTable3 tableModel, int sel)
    {
        //получить строку выделенную
        int InRow = table.getSelectedRow();
        int row = table.convertRowIndexToModel(InRow);
        //получить выделенную строку
        ArrayList<Object> Row = tableModel.getRowAt(row);
//=======================================================
        try
        {
            sql.selValue(Row, sel);
        }
        catch (SQLException e2)
        {
            System.out.println(e2.getMessage());
        }
//=======================================================
    }

    //создание таблицы
    private void CreateTable(JTable table, JPanel TablePanel)
    {

        //таблица во всю панель
        table.setFillsViewportHeight(true);

        //scrollPane - таблица
        JScrollPane scrollPane = new JScrollPane(table);

        TablePanel.setLayout(new BorderLayout());
        TablePanel.add(scrollPane, BorderLayout.CENTER);
    }


    //обновление вкладок с таблицами и связанных таблиц
    private void updateTable(int sel)
    {
        switch (sel)
        {

            case 1:
                //ресурс
                ChangeTable(tableRes, 2);
                break;
            case 3:
                //задачи
                ChangeTable(tableTask, 3);
                //ресурсов в задаче(работ)
                ChangeTable(tableWRes, 4);
                break;
            case 4:
                //ресурсов в задаче(работ)
                ChangeTable(tableWRes, 4);
                break;
        }
    }

    //проверка дат между датами
    private String dateExecution(Date date, int yes, String value)
    {
        Date min;
        Date max;
        String whichDate = null;

        try
        {
            switch (yes)
            {
                //если задача
                case 3:
                    min = (Date)sql.selectValue(222).get(0).get(2);
                    max = (Date)sql.selectValue(222).get(0).get(3);
                    if(!dateBetweenDates(date, min, max))
                        whichDate = "проекта: '" + min + "'-'" + max + "'";
                    return whichDate;

                //если работа с ресурсами
                case 4:
                    sql.setTypeTask(value);
                    sql.setIdTask((Integer)sql.selectValue(33).get(0).get(0));
                    min = (Date)sql.selectValue(30).get(0).get(1);
                    max = (Date)sql.selectValue(30).get(0).get(2);
                    if(!dateBetweenDates(date, min, max))
                        whichDate = "задачи: '" + min + "'-'" + max + "'";
                    return whichDate;

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return whichDate;
    }


    //дата между дат?
    private boolean dateBetweenDates(Date d, Date min, Date max)
    {
        return d.compareTo(min) == 0 || d.compareTo(max) == 0
                || d.after(min) && d.before(max);
    }

    //временные рамки задачи
    private String getDateTask(String name)
    {

        Date min, max;
        int id;
        String Value = "";
        //установить название задачи
        sql.setTypeTask(name);
        //получить id задачи
        try
        {
            id = (Integer)sql.selectValue(33).get(0).get(0);
            sql.setIdTask(id);
            //получить даты начала и окончания задачи
            min = (Date)sql.selectValue(30).get(0).get(1);
            max = (Date)sql.selectValue(30).get(0).get(2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Value = "'"+simpleDateFormat.format(min)+"'-'"+simpleDateFormat.format(max)+"'";
        }
        catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Value;

    }

    //проверка и добавление уникальности в таблицы
    private ArrayList<Object> uniqueType(int sel, ArrayList<Object> strings, boolean ch)
    {
        ArrayList<ArrayList<Object>> data;
        boolean value = false;
        Object[] options = {"Да","Нет"};
        int k = 1;
        try
        {
            switch (sel)
            {
                //ресурс
                case 1:
                    //получаем имя добавляемого клиента
                    String newN = strings.get(0).toString();

                    String oldN = "";
                    //массив контактов
                    data = sql.selectValue(2);

                    //удаление текущего если изменяешь текущее значение
                    if(ch)
                    {
                        int id;
                        int qid;
                        id = sql.getIdRes();
                        for(int i=0;i<data.size();i++)
                        {
                            qid = (Integer)data.get(i).get(3);
                            if(qid==id) data.remove(i);
                       }
                    }

                    for(int i=0;i<data.size();i++)
                    //проходит по всему массиву строк
                    {
                        if(data.get(i).get(0).equals(newN))
                        //если есть совпадение
                        {
                            if(!value)
                            {
                                //если содержит (*)
                                newN = newN.replaceAll("\\(\\d+\\)", "");
                            }
                            value = true;
                            //если строка не пустая мы прозодим не первый раз
                            if(!oldN.isEmpty())
                            {
                                //значит возвращаем значение без id
                                newN=oldN;
                            }
                            //присваивается старое значение снова без id
                            oldN = newN;
                            newN = newN + "("+k+")";
                            k++; //добавляем (1)->(2)
                            i=0; //возвращаем значение для повторной проверки,
                            // чтобы не было повторений после изменений
                        }
                    }

                    strings.set(0, newN);
                    if(value)
                    {
                        String name = "й пользователь";
                        int reply = JOptionPane.showOptionDialog(null,
                                "Тако"+name+" уже существует. " +
                                        "Добавить, изменив имя?",
                                "Проверка", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null, options, options[1]);

                        if (reply == JOptionPane.NO_OPTION)
                        {
                            return null;
                        }
                    }
                    break;

                //==============================================================
                //Задача
                //==============================================================
                case 3:
                    //получаем описание добавляемой задачи
                    String newNTask = strings.get(0).toString();
                    String nameFav = strings.get(4).toString();


                    String oldNTask = "";
                    //массив работ
                    data = sql.selectValue(3);


                    //удаление текущего если изменяешь текущее значение
                    if(ch)
                    {
                        int id = sql.getIdTask();
                        int qid;
                        for(int i=0;i<data.size();i++)
                        {
                            qid = (Integer)data.get(i).get(7);
                            if(qid==id) data.remove(i);
                        }
                    }


                    for(int i=0;i<data.size();i++)
                    //проходит по всему массиву строк
                    {
                        if(data.get(i).get(0).equals(newNTask)&&
                                data.get(i).get(4).equals(nameFav))
                        //если есть совпадение
                        {
                            if(!value)
                            {
                                //если содержит (*)
                                newNTask = newNTask.replaceAll("\\(\\d+\\)", "");//)
                            }
                            value = true;
                            //если строка не пустая мы прозодим не первый раз
                            if(!oldNTask.isEmpty())
                            {
                                //значит возвращаем значение без id
                                newNTask=oldNTask;
                            }
                            //присваивается старое значение снова без id
                            oldNTask = newNTask;
                            newNTask = newNTask + "("+k+")";
                            k++; //добавляем (1)->(2)
                            i=0; //возвращаем значение для повторной проверки,
                            // чтобы не было повторений после изменений
                        }
                    }

                    strings.set(0, newNTask);
                    if(value)
                    {
                        int reply = JOptionPane.showOptionDialog(null,
                                "Такая задача с таким ответственным уже " +
                                        "существует в данном проекте. " +
                                        "Добавить, изменив имя?",
                                "Проверка", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null, options, options[1]);

                        if (reply == JOptionPane.NO_OPTION)
                        {
                            return null;
                        }
                    }
                    break;
                //==============================================================
                //Работа с ресурсами
                //==============================================================
                case 4:
                    //получаем описание добавляемой задачи
                    String newNWR = strings.get(2).toString();
                    String nameRes = strings.get(5).toString();
                    String nameTask = strings.get(6).toString();

                    String oldNWR = "";
                    //массив работ
                    data = sql.selectValue(4);

                    //удаление текущего если изменяешь текущее значение
                    if(ch)
                    {
                        int id = sql.getIdWork();
                        int qid;
                        for(int i=0;i<data.size();i++)
                        {
                            qid = (Integer)data.get(i).get(9);
                            if(qid==id) data.remove(i);
                        }
                    }

                    for(int i=0;i<data.size();i++)
                    //проходит по всему массиву строк
                    {
                        if(data.get(i).get(2).equals(newNWR)&&
                                data.get(i).get(5).equals(nameRes)&&
                                data.get(i).get(6).equals(nameTask))
                        //если есть совпадение
                        {
                            if(!value)
                            {
                                //если содержит (*)
                                newNWR = newNWR.replaceAll("\\(\\d+\\)", "");//)
                            }
                            value = true;
                            //если строка не пустая мы прозодим не первый раз
                            if(!oldNWR.isEmpty())
                            {
                                //значит возвращаем значение без id
                                newNWR=oldNWR;
                            }
                            //присваивается старое значение снова без id
                            oldNWR = newNWR;
                            newNWR = newNWR + "("+k+")";
                            k++; //добавляем (1)->(2)
                            i=0; //возвращаем значение для повторной проверки,
                            // чтобы не было повторений после изменений
                        }
                    }

                    strings.set(2, newNWR);
                    if(value)
                    {
                        int reply = JOptionPane.showOptionDialog(null,
                                "Такая работа с данным пользователем уже " +
                                        "существует в данной задаче. " +
                                        "Добавить, изменив имя?",
                                "Проверка", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null, options, options[1]);

                        if (reply == JOptionPane.NO_OPTION)
                        {
                            return null;
                        }
                    }
                    break;
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return strings;
    }

    //Большие буквы
    public static String capitalizeString(String string)
    {
        //char[] chars = string.toLowerCase().toCharArray();
        char[] chars = string.toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }


}
