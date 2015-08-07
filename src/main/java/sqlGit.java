import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class sqlGit - create connection with MySQL database
 * using JDBC driver
 * control current repositories GIT
 * @author seth-list
 * @version 2014 summer
*/
public class sqlGit
{

    //Driver and URL for database
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String JDBC_URL = "jdbc:mysql://localhost/crmgit";

    //USER and PASSWORD for database connection
    private static String JDBC_USER = "";
    private static String JDBC_PASSWORD = "";

    //current id parameters
    public int idManager = 0;
    public int idPr = 0;
    public int idTask = 0;
    public int idRes = 0;
    public int idWork = 0;

    //current name of Task and Resource
    public String typeTask = "";
    public String nameRes = "";

    /**
     *  parameter for reloading current repositories
     *  using only one time, after that its become false
    */
    public boolean need = false;

    //current connection for database
    private Connection dbConnection = null;

    //file for loading USER and PASSWORD for connection
    private FileWork file = new FileWork();


    /**
     * setter
     * @param id - set id
     */
    //set value
    public void setIdPr(int id)
    {
        idPr = id;
    }

    //set value
    public void setNeed(boolean id)
    {
        need = id;
    }

    //set value
    public void setIdTask(int id)
    {
        idTask = id;
    }

    //set value
    public int getIdTask()
    {
        return idTask;
    }

    //set value
    public int getIdRes()
    {
        return idRes;
    }

    //set value
    public int getIdWork()
    {
        return idWork;
    }

    //set value
    public void setTypeTask(String id)
    {
        typeTask = id;
    }

    //get value
    public String getValue(int sel)
    {
        switch (sel)
        {
            case 1: return nameRes;
            case 3: return typeTask;
        }
        return null;
    }

    //================================================


    /**
     * Read connection data from file - USER, PASSWORD, DATABASE name
     * Make connection
     * Using parameters - search data User and passUser in database
     * if parameters is found then return true
     * @param User - login of manager
     * @param passUser - password of manager
     * @return true if connection is successful and data was read from file
     */
    public boolean setLoginPass(String User, String passUser)
    {
        ArrayList<String> data = null;
        try
        {
            data = file.readFile();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
        }
        if(data==null) return false;

        //права на вход в БД
        JDBC_USER = data.get(0);
        JDBC_PASSWORD = data.get(1);

        //попробывать установить соединение
        dbConnection = getDBConnection();

        //если соединенение установлено
        if(dbConnection!=null)
        {
            //пользователь который входит
            ArrayList<Object> newUser = new ArrayList<Object>();
            newUser.add(User);
            newUser.add(passUser);

            //существующие пользователи
            ArrayList<ArrayList<Object>>  Users = new ArrayList<ArrayList<Object>>();
            try
            {
                Users = selectValue(11);
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
            }

            //если такого пользователя нет, то:
            return Users.contains(newUser);
        }
        return false;
    }


    /**
     * Select statement - by parameter
     * dbConnection - createStatement
     * Using prepared statement
     * get ResultSet and add to returning value
     * finally - statement close
     * @param sel - index of selected statement
     * @return selected data
     * @throws SQLException
     */
    public ArrayList<ArrayList<Object>>  selectValue(int sel) throws SQLException
    {

        Statement stmt = null;
        String query = "";

        //выборка данных
        ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();

        //переход по индексу
        switch (sel)
        {
            //если 0 то таблица проект
            case 0:  query = "SELECT nameProj, idManager, " +
                    "begDate, endDate, shDes, budget, nameRepo" +
                    ", pathRepo, idProj " +
                    "FROM crmgit.project";
                break;
            //если 111 то таблица проект
            case 111:  query = "SELECT nameRepo, pathRepo, " +
                    "nameProj, typeRepo, commentary " +
                    "FROM crmgit.project WHERE nameRepo!=''";
                break;
            //если 222 то таблица проект
            case 222:  query = "SELECT nameProj, idManager, " +
                    "begDate, endDate, shDes, budget, nameRepo, pathRepo " +
                    "FROM crmgit.project where idProj="+idPr;
                break;
            //если 1 то таблица пользователь
            case 1:  query = "SELECT nameRes, typeRes, " +
                    "cost, login, pass " +
                    "FROM crmgit.res WHERE idRes="+idManager;
                break;
            //если 1 то таблица пользователь
            case 10:  query = "SELECT idRes FROM" +
                    " crmgit.res WHERE nameRes="+"'"+nameRes+"'";
                break;
            //если 11 то таблица пользователь
            case 11:  query = "SELECT login, " +
                    "pass FROM crmgit.res " +
                    "WHERE login!=''";
                break;
            //если 2 то таблица ресурс
            case 2:  query = "SELECT nameRes, " +
                    "cost, login, idRes " +
                    "FROM crmgit.res WHERE login!=''";
                break;
            //если 20 то таблица ресурс
            case 20:  query = "SELECT nameRes, cost "+
                    "FROM res WHERE idRes ="+idRes;
                break;
            //если 3 то таблица задача
            case 3:  query = "SELECT typeTask, " +
                    "begDate, endDate," +
                    "shDes, idRespTask, statusTask, " +
                    "nameBranch, idTask " +
                    "FROM task WHERE idProj ="+idPr;
                break;
            //если 30 то таблица задача
            case 30:  query = "SELECT typeTask, begDate, " +
                    "endDate, nameBranch "+
                    "FROM task WHERE idTask ="+idTask;
                break;
            //idTask по имени
            case 31:  query = "SELECT typeTask "+
                    "FROM task where idTask="+idTask+
                    " AND idProj="+idPr;
                break;
            //id задачи по имени задачи
            case 33:  query = "SELECT idTask "+
                    "FROM task where " +
                    "typeTask="+"'"+typeTask+"'"+
                    " AND idProj="+idPr;;
                break;
            //если 4 то таблица работа с ресурсами
            case 4:  query = "SELECT begDate, " +
                    "endDate, typWork," +
                    "resultWork, statWork, idRes, " +
                    "gain, idTask, idCommit, idWork " +
                    "FROM workres where idProj="+idPr;
                break;
            //если 41 то таблица работа с ресурсами
            case 41:  query = "SELECT begDate, " +
                    "endDate, typWork," +
                    "resultWork, statWork, idRes, " +
                    "gain, idTask, idCommit " +
                    "FROM workres where idWork="+idWork;
                break;


        }

        try
        {

            //созается утверждение
            stmt = dbConnection.createStatement();
            //получается результат
            ResultSet rs = stmt.executeQuery(query);

            ArrayList<ArrayList<Object>> arrayList;

            int i = 0;
            while (rs.next())
            {

                ArrayList<Object> Str = new ArrayList<Object>();
                //данные в зависимости от индекса
                switch (sel)
                {
                    //если 0 то таблица проект
                    case 0:
                        String nameProj = rs.getString("nameProj");
                        Str.add(nameProj);
                        int manager = rs.getInt("idManager");
                        idManager = manager;
                        arrayList = selectValue(1);
                        Str.add(arrayList.get(0).get(0));
                        Date begDate = rs.getDate("begDate");
                        Str.add(begDate);
                        Date endDate = rs.getDate("endDate");
                        Str.add(endDate);
                        String commentary = rs.getString("shDes");
                        Str.add(commentary);
                        double budget = rs.getDouble("budget");
                        Str.add(budget);
                        String nameRepo = rs.getString("nameRepo");
                        String pathRepo = rs.getString("pathRepo");

                        idPr = rs.getInt("idProj");

                        //если нужно то проверка существования хранилищ
                        if(need)  //need == true только при первой загрузке
                        {
                            //если хранилища правда нет
                            if(deleteNonExistingRepo(nameRepo, pathRepo))
                            //получение имени после обновления
                            nameRepo = "";
                        }
                        //добавить имя хранилища
                        if(!nameRepo.equals(""))
                        Str.add(nameRepo);
                        else
                        Str.add("-");
                        break;
                    //если 111 то таблица проект
                    case 111:
                        nameRepo = rs.getString("nameRepo");
                        Str.add(nameRepo);
                        pathRepo = rs.getString("pathRepo");
                        Str.add(pathRepo);
                        nameProj = rs.getString("nameProj");
                        Str.add(nameProj);
                        String typeRepo = rs.getString("typeRepo");
                        Str.add(typeRepo);
                        commentary = rs.getString("commentary");
                        Str.add(commentary);
                        break;
                    //если 222 то таблица проект
                    case 222:
                        nameProj = rs.getString("nameProj");
                        Str.add(nameProj);
                        manager = rs.getInt("idManager");
                        idManager = manager;
                        arrayList = selectValue(1);
                        Str.add(arrayList.get(0).get(0));
                        begDate = rs.getDate("begDate");
                        Str.add(begDate);
                        endDate = rs.getDate("endDate");
                        Str.add(endDate);
                        commentary = rs.getString("shDes");
                        Str.add(commentary);
                        budget = rs.getDouble("budget");
                        Str.add(budget);
                        nameRepo = rs.getString("nameRepo");
                        Str.add(nameRepo);
                        pathRepo = rs.getString("pathRepo");
                        Str.add(pathRepo);
                        break;
                    //если 1 то таблица ресурс
                    case 1:
                        String nameRes = rs.getString("nameRes");
                        Str.add(nameRes);
                        String typeRes = rs.getString("typeRes");
                        Str.add(typeRes);
                        Double cost = rs.getDouble("cost");
                        Str.add(cost);
                        String login = rs.getString("login");
                        Str.add(login);
                        String pass = rs.getString("pass");
                        Str.add(pass);
                        break;
                    case 10:
                        int idRs = rs.getInt("idRes");
                        Str.add(idRs);
                        break;
                    //если 11 то таблица ресурс
                    case 11:
                        login = rs.getString("login");
                        Str.add(login);
                        pass = rs.getString("pass");
                        Str.add(pass);
                        break;
                    //если 2 то таблица ресурс
                    case 2:
                        nameRes = rs.getString("nameRes");
                        Str.add(nameRes);
                        cost = rs.getDouble("cost");
                        Str.add(cost);
                        login = rs.getString("login");
                        Str.add(login);
                        idRs = rs.getInt("idRes");
                        Str.add(idRs);
                        break;
                    //если 20 то таблица ресурс
                    case 20:
                        String nameRs = rs.getString("nameRes");
                        Str.add(nameRs);
                        float cost1 = rs.getFloat("cost");
                        Str.add(cost1);
                        break;
                    //если 3 то таблица задача
                    case 3:
                        String typeTk = rs.getString("typeTask");
                        Str.add(typeTk);
                        Date dateBeginTask = rs.getDate("begDate");
                        Str.add(dateBeginTask);
                        Date dateEndTask = rs.getDate("endDate");
                        Str.add(dateEndTask);
                        String shortDTask = rs.getString("shDes");
                        Str.add(shortDTask);

                        manager = rs.getInt("idRespTask");
                        idManager = manager;
                        arrayList = selectValue(1);
                        Str.add(arrayList.get(0).get(0));

                        String statusTask = rs.getString("statusTask");
                        if(!statusTask.equals(""))
                        Str.add(statusTask);
                        else
                        Str.add("-");

                        String nameBranch = rs.getString("nameBranch");
                        if(!nameBranch.equals(""))
                        Str.add(nameBranch);
                        else
                        Str.add("-");
                        int id3 = rs.getInt("idTask");
                        Str.add(id3);
                        break;
                    //если 30 то таблица задача
                    case 30:
                        typeTk = rs.getString("typeTask");
                        Str.add(typeTk);
                        begDate = rs.getDate("begDate");
                        Str.add(begDate);
                        endDate = rs.getDate("endDate");
                        Str.add(endDate);
                        nameBranch = rs.getString("nameBranch");
                        if(!nameBranch.equals(""))
                        Str.add(nameBranch);
                        else
                        Str.add("Нет ветки");
                        break;
                    //если 31 то таблица задача
                    case 31:
                        String tTask = rs.getString("typeTask");
                        Str.add(tTask);
                        break;
                    //если 33 то таблица задача
                    case 33:
                        id3 = rs.getInt("idTask");
                        Str.add(id3);
                        break;
                    //если 4 то таблица работа с ресурсами
                    case 4:
                            //получение i-го значения idTask
                            idManager = rs.getInt("idRes");
                            //login = selectValue(1).get(0).get(3).toString();
                            //если в базе данных у данного пользователя нет логина
                            //то его работы не появляются в модуле работы с версиями
                             if (selectValue(1).get(0).get(3)!=null)
                            //if (!login.equals(""))
                            {
                                idRes = rs.getInt("idRes");
                                String name = selectValue(20).get(0).get(0).toString();
                                dateBeginTask = rs.getDate("begDate");
                                Str.add(dateBeginTask);
                                dateEndTask = rs.getDate("endDate");
                                Str.add(dateEndTask);
                                String typWork = rs.getString("typWork");
                                Str.add(typWork);
                                String resWork = rs.getString("resultWork");
                                Str.add(resWork);
                                String statWork = rs.getString("statWork");
                                Str.add(statWork);
                                Str.add(name);

                                int id2 = idTask;
                                idTask = rs.getInt("idTask");
                                String nameTask1 = selectValue(31).get(0).get(0).toString();
                                Str.add(nameTask1);
                                idTask = id2;

                                String commit = rs.getString("idCommit");
                                if (commit!=null)
                                Str.add(commit);
                                else
                                Str.add("Нет события");

                                idRs = rs.getInt("idRes");
                                Str.add(idRs);
                                idRs = rs.getInt("idWork");
                                Str.add(idRs);
                                break;
                            }
                        break;
                    //если 41 то таблица работа с ресурсами
                    case 41:
                                idRes = rs.getInt("idRes");
                                String name = selectValue(20).get(0).get(0).toString();
                                dateBeginTask = rs.getDate("begDate");
                                Str.add(dateBeginTask);
                                dateEndTask = rs.getDate("endDate");
                                Str.add(dateEndTask);
                                String typWork = rs.getString("typWork");
                                Str.add(typWork);
                                String resWork = rs.getString("resultWork");
                                Str.add(resWork);
                                String statWork = rs.getString("statWork");
                                Str.add(statWork);
                                Str.add(name);

                                int id2 = idTask;
                                idTask = rs.getInt("idTask");
                                String nameTask1 = selectValue(31).get(0).get(0).toString();
                                Str.add(nameTask1);
                                idTask = id2;

                                String commit = rs.getString("idCommit");
                                Str.add(commit);

                                idRs = rs.getInt("idRes");
                                Str.add(idRs);
                                id2 = rs.getInt("idTask");
                                Str.add(id2);
                                break;
                }
                //добавить в данные о таблице
                if(!Str.isEmpty())
                {
                    data.add(i, Str);
                    i++;
                }
            }
        }
        catch (SQLException e )
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
        }
        finally
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }
        return data;
    }

    /**
     * Add value to database
     * Using prepared statement
     * @param row - added value
     * @param sel - index of selected statement
     * @return true if adding is successful
     * @throws SQLException
     */
    public boolean addValue(ArrayList<Object> row, int sel) throws SQLException
    {

        //приготовленная строка sql
        PreparedStatement preparedStatement = null;
        //строка
        String insertTableSQL = null;

        switch (sel)
        {
            case 0:
                insertTableSQL = "UPDATE crmgit.project SET "
                        + "nameRepo=?, pathRepo=?, " +
                        "typeRepo=?, commentary=? " +
                        "where idProj="+idPr;
                break;
            case 1:
                insertTableSQL = "INSERT INTO res"
                        + "(nameRes, typeRes, " +
                        "cost, login, pass ) VALUES" +
                        "(?,?,?,?,?)";
                break;
            case 3:
                insertTableSQL = "INSERT INTO task"
                        + "(typeTask, begDate, " +
                        "endDate, shDes, " +
                        "idRespTask, idProj, " +
                        "statusTask, designPhase) VALUES" +
                        "(?,?,?,?,?,?,?,?)";
                break;
            case 4:
                insertTableSQL = "INSERT INTO workres"
                        + "(begDate, " +
                        "endDate, typWork, " +
                        "resultWork, statWork, " +
                        "idRes, idTask, gain, idCommit, idProj) VALUES" +
                        "(?,?,?,?,?,?,?,?,?,?)";
                break;
            case 5:
                insertTableSQL = "UPDATE crmgit.task SET "
                        + "nameBranch=?" +
                        "where idTask="+idTask;
                break;
            case 6:
                insertTableSQL = "UPDATE crmgit.task SET "
                        + "nameBranch=?" +
                        "where idProj="+idPr;
                break;
            case 7:
                insertTableSQL = "UPDATE crmgit.workres SET "
                        + "idCommit=?" +
                        "where idWork="+idWork;
                break;
            case 8:
                insertTableSQL = "UPDATE crmgit.workres SET "
                        + "idCommit=?" +
                        "where idTask="+idTask;
                break;
        }


        try
        {
            preparedStatement = dbConnection.prepareStatement(insertTableSQL);

            switch (sel)
            {
                case 0:
                    //добавление в таблицу repository
                    preparedStatement.setString(1, row.get(0).toString());
                    //preparedStatement.setBoolean(2, Boolean.parseBoolean(row.get(1).toString()));
                    preparedStatement.setString(2, row.get(2).toString());
                    preparedStatement.setString(3, row.get(3).toString());
                    preparedStatement.setString(4, row.get(4).toString());
                    break;
                case 1:
                    //добавление в таблицу res
                    preparedStatement.setString(1, row.get(0).toString());
                    preparedStatement.setString(2, "Пользователь");
                    int intCost = (Integer)row.get(1);
                    preparedStatement.setInt(3, intCost);
                    preparedStatement.setString(4, row.get(2).toString());
                    preparedStatement.setString(5, row.get(3).toString());
                    break;
                case 3:
                    //добавление в таблицу task
                    //сравнение дат
                    if(!dateWork.compareDate(row.get(1), row.get(2)))
                    return false;

                    //название задачи
                    preparedStatement.setString(1, row.get(0).toString());
                    //две даты
                    Date sqlDate1 = dateWork.objectToDate(row.get(1));
                    Date sqlDate2 = dateWork.objectToDate(row.get(2));
                    preparedStatement.setDate(2, sqlDate1);
                    preparedStatement.setDate(3, sqlDate2);

                    //краткое описание
                    preparedStatement.setString(4, row.get(3).toString());
                    //ответственный
                    //по имени ресурса получить id
                    nameRes = row.get(4).toString();
                    int id = (Integer)selectValue(10).get(0).get(0);
                    //записать id ответственного
                    preparedStatement.setInt(5, id);
                    //=========================================

                    //число - idProject
                    preparedStatement.setInt(6, idPr);

                    typeTask = row.get(0).toString();

                    preparedStatement.setString(7, row.get(5).toString());
                    preparedStatement.setString(8, "Технический проект");

                    //preparedStatement.setString(8, row.get(6).toString());

                    break;
                case 4:
                    //добавление в таблицу workres
                    //две даты
                    if(!dateWork.compareDate(row.get(0), row.get(1)))
                    return false;

                    //запись двух дат
                    Date begDate = dateWork.objectToDate(row.get(0));
                    Date endDate = dateWork.objectToDate(row.get(1));
                    preparedStatement.setDate(1, begDate);
                    preparedStatement.setDate(2, endDate);

                    //три строки - тип, статус, результат
                    preparedStatement.setString(3, row.get(2).toString());
                    preparedStatement.setString(4, row.get(3).toString());
                    preparedStatement.setString(5, row.get(4).toString());

                    //получить id по названию ресурса
                    nameRes = row.get(5).toString();
                    id = (Integer)selectValue(10).get(0).get(0);
                    preparedStatement.setInt(6, id);

                    //получить id задачи по названию
                    typeTask =  row.get(6).toString();
                    id = (Integer)selectValue(33).get(0).get(0);
                    preparedStatement.setInt(7, id);

                    //================================
                    //прибыль
                    preparedStatement.setFloat(8, 0);
                    //commit
                    preparedStatement.setString(9, "");
                    //число - idProject
                    preparedStatement.setInt(10, idPr);
                    break;
                case 5:
                    //добавление в таблицу Branch
                    preparedStatement.setString(1, row.get(0).toString());
                    break;
                case 6:
                    //добавление в таблицу Branch
                    preparedStatement.setString(1, row.get(0).toString());
                    break;
                case 7:
                    //добавление в таблицу Commit
                    preparedStatement.setString(1, row.get(0).toString());
                    break;
                case 8:
                    //добавление в таблицу Commit
                    preparedStatement.setString(1, row.get(0).toString());
                    break;

            }

            // execute insert SQL stetement
            preparedStatement.executeUpdate();

            //JOptionPane.showMessageDialog(new JPanel(), "Запись добавлена в таблицу!");
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
        }
        finally
        {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

        }
        return true;
    }

    /**
     * Delete value from database
     * ID is known
     * @param sel - index of selected statement
     * @throws SQLException
     */
    public void delValue(int sel) throws SQLException
    {

        //приготовленная строка для удаления
        PreparedStatement preSt = null;
        //строка sql для удаления из таблицы проект строки
        String deleteTableSQL = null;

        switch (sel)
        {
            //если 1 то таблица ресурс
            case 1:
                deleteTableSQL = "DELETE FROM res " +
                        "WHERE idRes = ?";

                break;
            //если 3 то таблица задача
            case 3:
                deleteTableSQL = "DELETE FROM task " +
                        "WHERE idTask = ?";

                break;
            //если 4 то таблица workres
            case 4:
                deleteTableSQL = "DELETE FROM workres " +
                        "WHERE idWork = ?";
                break;
      }

        try {


                    switch (sel)
                    {
                        //если 1 то таблица ресурс
                        case 1:
                            //приготовить на выполнение удаления
                            preSt = dbConnection.prepareStatement(deleteTableSQL);
                            preSt.setInt(1, idRes);
                            break;
                        //если 3 то таблица задача
                        case 3:
                            //приготовить на выполнение удаления
                            preSt = dbConnection.prepareStatement(deleteTableSQL);
                            preSt.setInt(1, idTask);
                            break;
                        //если 4 то таблица workres
                        case 4:
                            //приготовить на выполнение удаления
                            preSt = dbConnection.prepareStatement(deleteTableSQL);
                            preSt.setInt(1, idWork);
                            break;
                    }
            if (preSt != null)
            {
                preSt.executeUpdate();//выполнить удаление
            }


            //JOptionPane.showMessageDialog(new JPanel(), "Запись удалена!");

        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());

        } finally
        {
            if (preSt != null) {preSt.close();}
        }
    }

    /**
     * change value in database
     * @param row - new value
     * @param sel - index of selected statement
     * @return true is success
     * @throws SQLException
     */
    public boolean changeValue(ArrayList<Object> row, int sel) throws SQLException
    {

        //приготовленная строка sql
        PreparedStatement preparedStatement = null;
        //строка
        String insertTableSQL = null;

        switch (sel) {
            //ресурс
            case 1:
                insertTableSQL = "UPDATE res SET"
                        + " nameRes=?, typeRes=?, " +
                        "cost=?, login=?, pass=?" +
                        " WHERE idRes="+idRes;
                break;
            //задача
            case 3:
                insertTableSQL = "UPDATE task SET "+
                "typeTask=?, begDate=?, " +
                "endDate=?, shDes=?, " +
                "idRespTask=?, idProj=?, " +
                "statusTask=?"+
                " where idTask="+idTask;
                break;
            //работа с ресурсом
            case 4:
                insertTableSQL = "UPDATE workres SET "
                        + "begDate=?, " +
                        "endDate=?, typWork=?, " +
                        "resultWork=?, statWork=?, " +
                        "idRes=?, idTask=?, gain=?"+
                        "where idWork="+idWork;
                break;
        }


        try {
            //dbConnection = getDBConnection();
            preparedStatement = dbConnection.prepareStatement(insertTableSQL);

            switch (sel)
            {
                case 1:
                    //res
                    preparedStatement.setString(1, row.get(0).toString());
                    preparedStatement.setString(2, "Пользователь");
                    int intCost = (Integer)row.get(1);
                    preparedStatement.setInt(3, intCost);
                    preparedStatement.setString(4, row.get(2).toString());
                    preparedStatement.setString(5, row.get(3).toString());
                    break;
                case 3:
                    //task
                    //сравнение дат
                    if(!dateWork.compareDate(row.get(1), row.get(2)))
                        return false;

                    //название задачи
                    preparedStatement.setString(1, row.get(0).toString());
                    //две даты
                    Date sqlDate1 = dateWork.objectToDate(row.get(1));
                    Date sqlDate2 = dateWork.objectToDate(row.get(2));
                    preparedStatement.setDate(2, sqlDate1);
                    preparedStatement.setDate(3, sqlDate2);

                    //краткое описание
                    preparedStatement.setString(4, row.get(3).toString());
                    //ответственный
                    //по имени ресурса получить id
                    nameRes = row.get(4).toString();
                    int id = (Integer)selectValue(10).get(0).get(0);
                    //записать id ответственного
                    preparedStatement.setInt(5, id);
                    //=========================================

                    //число - idProject
                    preparedStatement.setInt(6, idPr);



                    typeTask = row.get(0).toString();

                    preparedStatement.setString(7, row.get(5).toString());
                    break;
                case 4:
                    //workres
                    //две даты
                    if(!dateWork.compareDate(row.get(0), row.get(1)))
                    return false;

                    //запись двух дат
                    Date begDate = dateWork.objectToDate(row.get(0));
                    Date endDate = dateWork.objectToDate(row.get(1));
                    preparedStatement.setDate(1, begDate);
                    preparedStatement.setDate(2, endDate);

                    //три строки - тип, статус, результат
                    preparedStatement.setString(3, row.get(2).toString());
                    preparedStatement.setString(4, row.get(3).toString());
                    preparedStatement.setString(5, row.get(4).toString());

                    //получить id по названию ресурса
                    nameRes = row.get(5).toString();
                    id = (Integer)selectValue(10).get(0).get(0);
                    preparedStatement.setInt(6, id);

                    //получить id задачи по названию
                    typeTask =  row.get(6).toString();
                    id = (Integer)selectValue(33).get(0).get(0);
                    preparedStatement.setInt(7, id);

                    //================================
                    //прибыль
                    preparedStatement.setFloat(8, 0);
                    break;
            }

            // execute insert SQL stetement
            preparedStatement.executeUpdate();

            //JOptionPane.showMessageDialog(new JPanel(), "Запись изменена!");
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());

        }
        finally
        {

            if (preparedStatement != null)
            {
                preparedStatement.close();
            }

        }
        return true;
    }

    /**
     * get index by value
     * @param row - value
     * @param sel - index of selected statement
     * @throws SQLException
     */
    public void selValue(ArrayList<Object> row, int sel) throws SQLException
    {

        //строка statement для выборки
        PreparedStatement stmt = null;
        //строка выбора для sql
        String query = null;


        switch (sel)
        {
            //если 0 то таблица проект
            case 0:

                query = "SELECT idProj " +
                        "FROM crmgit.project WHERE nameProj=? ";
                break;
            //если 1 то таблица ресурс
            case 1:
                //удаляет все ресурсы одинаковые
                query = "SELECT idRes " +
                        "FROM res WHERE nameRes=? ";
                break;
            //если 3 то таблица задача
            case 3:
                query = "SELECT idTask " +
                        "FROM task WHERE typeTask=? " +
                        "AND idProj=?";
                break;
            case 4:
                query = "SELECT idWork " +
                        "FROM workres WHERE typWork=? " +
                        "AND idRes=? AND idTask=?";
                break;
        }


        try
        {

            //выборка
            stmt =  dbConnection.prepareStatement(query);
            switch (sel) {
                //если 0 то таблица проект
                case 0:
                    stmt.setString(1, row.get(0).toString());
                    break;
                //если 1 то таблица ресурс
                case 1:
                    stmt.setString(1, row.get(0).toString());
                    break;
                //если 3 то таблица задача
                case 3:
                    stmt.setString(1, row.get(0).toString());
                    stmt.setInt(2, idPr);
                    break;
                //если 4 то таблица работа с ресурсами
                case 4:
                    stmt.setString(1, row.get(2).toString());

                    //получить id по названию ресурса
                    nameRes = row.get(5).toString();
                    int id = (Integer)selectValue(10).get(0).get(0);
                    stmt.setInt(2, id);

                    typeTask =  row.get(6).toString();
                    id = (Integer)selectValue(33).get(0).get(0);
                    stmt.setInt(3, id);
                    break;
            }

            // execute select SQL statement
            //получить выборку
            ResultSet rs = stmt.executeQuery();

            int i = 0;
            while (rs.next())
            {
                switch (sel)
                {
                    //если 0 то таблица проект
                    case 0:
                        //получение id_of_project при вызове функции
                        idPr = rs.getInt("idProj");
                        break;
                    case 1:
                        idRes = rs.getInt("idRes");
                        break;
                    case 3:
                        idTask = rs.getInt("idTask");
                        break;
                    case 4:
                        idWork = rs.getInt("idWork");
                        break;
                }

                i++;
            }

            //JOptionPane.showMessageDialog(new JPanel(), "Запись удалена!");

        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());

        }
        finally
        {

            if (stmt != null)
            {
                stmt.close();
            }
        }
    }

    /**
     * delete non existing repo - first time loading
     * using
     * @see gitSend
     * update database if repositoty is nonexisting
     * @param nameRepo - name of repository
     * @param pathRepo - path to repository
     * @return true if repository deleted
     */
    private boolean deleteNonExistingRepo(String nameRepo, String pathRepo)
    {

        if(nameRepo.contains("(Копия)"))
        {
            nameRepo = nameRepo.replace("(Копия)", "");
        }
        File targetDir = new File(pathRepo+"\\"+nameRepo+"\\.git");
        //если репозиторий


        if(!nameRepo.equals(""))
        if (gitSend.isRepository(targetDir))
        {
            return false;
        }
        else
        {
            ArrayList<Object> data = new ArrayList<Object>();
            ArrayList<ArrayList<Object>> task;
            for (int i = 0;i<5;i++)
            {
                data.add("");
            }
            try
            {
                //обновить проект
                addValue(data, 0);
                //обнулить ветки
                addValue(data, 6);
                //обнулить коммиты

                //все задачи в проекте
                task = selectValue(3);

                for (ArrayList<Object> aTask : task)
                {
                    int id = idTask;
                    idTask = (Integer) aTask.get(8);
                    addValue(data, 8);
                    idTask = id;
                }


            }
            catch (SQLException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //вернуть правду
            return true;

        }

        return false;

    }

    /**
     * set connection to database one time - static
     * use - JDBC_DRIVER
     * @return Connection if successful
     */
    private static Connection getDBConnection()
    {
        Connection dbConnection = null;
        try
        {
            //подключение класса
            Class.forName(JDBC_DRIVER);
        }
        catch (ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
        }

        try
        {
            //установить соединение
            dbConnection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            return dbConnection;

        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
        }

        return dbConnection;
    }

}
