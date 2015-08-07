import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for working with GIT
 * @author seth-list
 */
public class gitSend
{
    //получить хранилище из данной директории
    public static Repository setRepo(File file)
    {
        Repository repository;
        //Git git = null;
        try
        {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            repository = builder.setGitDir(file).setMustExist(true).build();
        }
        catch (IOException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
            return null;
        }

        return repository;
    }


    //список событий для данной ветки
    public static ArrayList<ArrayList<Object>> listCommit(Repository repo, String nameBranch,
                                                    String nameManager)
    {

        if(repo == null)
        {
            return null;
        }

        Git git = new Git(repo);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);

        //данные для коммитов
        ArrayList<ArrayList<Object>> commitsData
                = new ArrayList<ArrayList<Object>>();

        try
        {

            RevWalk walk = new RevWalk(repo);

            //получение всех веток
            List<Ref> branches = git.branchList().call();

            for (Ref branch : branches)
            {

                String branchName = branch.getName();
                //если ветка та самая
                if(branchName.equals(nameBranch))
                {

                    //то получение всех коммитов
                    Iterable<RevCommit> commits = git.log().all().call();

                    for (RevCommit commit : commits)
                    {
                        boolean foundInThisBranch = false;

                        RevCommit targetCommit
                                = walk.parseCommit(repo.resolve(commit.getName()));

                        for (Map.Entry<String, Ref> e1 :
                                repo.getAllRefs().entrySet())
                        {
                            if (e1.getKey().startsWith(Constants.R_HEADS))
                            {
                                if (walk.isMergedInto(targetCommit,
                                        walk.parseCommit(e1.getValue().getObjectId())))
                                {

                                    String foundInBranch = e1.getValue().getName();

                                    if (branchName.equals(foundInBranch))
                                    {
                                        foundInThisBranch = true;
                                        break;
                                    }
                                }
                            }
                        }

                        //если коммит принадлежит ветке
                        //то добавление в таблицу
                        if (foundInThisBranch)
                        {

                            ArrayList<Object> newRow = new ArrayList<Object>();
                            newRow.add(commit.getFullMessage());
                            newRow.add(commit.getAuthorIdent().getName());
                            try
                            {
                                Date parsedDate = sdf.parse(commit
                                        .getAuthorIdent()
                                        .getWhen().toString());

                                String date = simpleDateFormat
                                        .format(parsedDate);

                                newRow.add(date);
                            }
                            catch (ParseException e1)
                            {
                                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                            if(nameManager==null)
                            {
                                commitsData.add(newRow);
                            }
                            //если пользователь коммита тот
                            else if(nameManager.equals(commit
                                    .getAuthorIdent()
                                    .getName()))
                            {
                                commitsData.add(newRow);
                            }

                            //System.out.println(commit.getName());
                        }
                    }
                }
            }
        }
        catch (GitAPIException e1)
        {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
        }

        return commitsData;
    }

    //список событий для данной ветки
    public static ArrayList<Object> getListBranch(Repository repo)
    {
        if(repo == null)
        {
            return null;
        }

        ArrayList<Object> listBranch = new ArrayList<Object>();
        List<Ref> call = null;

        try
        {
            call = new Git(repo).branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        }
        catch (GitAPIException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
        }

        if(call!=null)
        for (Ref ref : call)
        {
            String name = ref.getName();
            listBranch.add(name);
        }

        return listBranch;

    }


    //клонирование репозитория
    public static boolean cloneRepository(boolean needBranch, String sourceDir,
                                       File targetDir, boolean global)
    {

        if(!sourceDir.startsWith("https://") && global)
        {
            JOptionPane.showMessageDialog(new JPanel(),
                    "Используйте HTTPS URL для клонирования удаленного хранилища!");
            return false;
        }
        try
        {

            if(needBranch)
            {
                Git.cloneRepository().setURI(sourceDir).
                        setDirectory(targetDir).setBranch("master").setBare(false).setRemote("origin").
                        setNoCheckout(false).call();

                // now open the created repository
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                builder.setGitDir(targetDir)
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir()      // scan up the file system tree
                        .build();

            }
            else
            {

                Git.cloneRepository()
                        .setURI(sourceDir)
                        .setDirectory(targetDir)
                        .call();

                //https://github.com/seth-list/repo-example.git
                // now open the created repository
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                builder.setGitDir(targetDir)
                        .readEnvironment() // scan environment GIT_* variables
                        .findGitDir()      // scan up the file system tree
                        .build();

            }
        }
        catch (TransportException e)
        {
            //TODO разобрать с этой ошибкой
            JOptionPane.showMessageDialog(new JPanel(), "Сайт не установлен в известных хостах в .ssh");
            return true;
        }
        catch (GitAPIException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
            return false;
        }
        catch (IllegalStateException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
            return false;
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
            return false;
        }
        catch (JGitInternalException e)
        {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
            return false;
        }
        return true;
    }

    //создать репозиторий
    public static boolean createRepository(File targetDir, String nameManager,
                                           String email)
    {
        Repository repo;
        try
        {

            Git.init()
                    .setDirectory(targetDir)
                    .call();

            FileRepositoryBuilder builder = new FileRepositoryBuilder();

            FileRepositoryBuilder.create(targetDir);

            File repoFile = new File(targetDir.getAbsolutePath()+"\\.git");

            repo = builder.setGitDir(repoFile)
                    .setMustExist(true).build();

            Git git = new Git(repo);



            PersonIdent personIdent
                    = new PersonIdent(nameManager, email);
            //первоначальный коммит
            git.commit().setMessage("Хранилище создано").setCommitter(personIdent).call();

            repo.close();


            git.branchCreate().setName("master")
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                        .setForce(true).call();


            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
                return false;
            }
            catch (IllegalStateException e1)
            {
                JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
                return false;
            }
            catch (GitAPIException e)
            {
                JOptionPane.showMessageDialog(new JPanel(), e.getMessage());
                return false;
            }
        return true;

    }


    //выбор ветки мастер
    public static void chooseMaster(Repository repo)
    {

        if(repo == null)
        {
            return;
        }

        //получение git
        Git git = new Git(repo);

        //выбор ветки мастер
        try
        {
            git.checkout().setName("master").call();
        }
        catch (GitAPIException e1)
        {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    //выбор ветки мастер
    public static void chooseBranch(Repository repo, String name)
    {

        if(repo == null)
        {
            return;
        }

        //получение git
        Git git = new Git(repo);

        //выбор ветки мастер
        try
        {
            git.checkout().setName(name).call();
        }
        catch (GitAPIException e1)
        {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //создание ветки
    public static boolean createBranch(Repository repo, String nameManager,
                                       String email, String name)
    {

         if(repo == null)
         {
             return false;
         }



        //получение git
        Git git = new Git(repo);
        try
        {

            //создать ветку
            git.branchCreate()
                    .setName(name)
                    .call();

            //выбор ветки / по умолчанию мастер
            git.checkout().setName(name).call();


            PersonIdent personIdent = new PersonIdent(nameManager, email);
            //Коммит создания ветки
            git.commit()
                    .setAuthor(personIdent)
                    .setCommitter(personIdent)
                    .setMessage("Ветка создана: " + name)
                    .call();

            //возвращение мастера
            //=============================
            //выбор ветки мастер
            git.checkout().setName("master").call();
            }
        catch (GitAPIException e1)
        {
           JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
           return false;
        }
        return true;

    }


    //получить список локальных веток
    public static ArrayList<String> getLocalBranches(Repository repo)
    {

        ArrayList<String> branches = new ArrayList<String>();
        if(repo == null)
        {
            return null;
        }

        List<Ref> call = null;

        try
        {
            call = new Git(repo).branchList().call();
        }
        catch (GitAPIException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
        }
        if (call != null)
        {
            for (Ref ref : call)
            {
                branches.add(ref.getName());
            }
        }
        return branches;
    }

    //получить список всех веток
    public static ArrayList<String> getAllBranches(Repository repo)
    {

        ArrayList<String> branches = new ArrayList<String>();
        if(repo == null)
        {
            return null;
        }

        List<Ref> call = null;

        try
        {
            call = new Git(repo).branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL).call();
        }
        catch (GitAPIException e1)
        {
            JOptionPane.showMessageDialog(new JPanel(), e1.getMessage());
        }
        if (call != null)
        {
            for (Ref ref : call)
            {
                branches.add(ref.getName());
            }
        }
        return branches;
    }


    //создать коммит
    public static boolean createCommit(Repository repo, String nameManager,
                                       String email, String name, String id)
    {

        if(repo == null)
        {
            return false;
        }

        Git git = new Git(repo);
        //пользователь добавляющий commit
        PersonIdent personIdent
                = new PersonIdent(nameManager, email);
        //Коммит создания ветки
        try
        {
            //выбор ветки / по умолчанию мастер
            git.checkout().setName(name).call();

            git.commit()
                    .setAuthor(personIdent)
                    .setCommitter(personIdent)
                    .setMessage("Работа: '"+id+
                            "' выполнена пользователем: '"+nameManager+"'")
                    .call();
        }
        catch (GitAPIException e1)
        {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;

    }


    public static boolean isRepository(File targetDir)
    {
        return RepositoryCache.FileKey.isGitRepository(targetDir, FS.DETECTED);
    }

}
