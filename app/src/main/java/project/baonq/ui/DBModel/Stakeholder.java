package project.baonq.ui.DBModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "stakeholder")
public class Stakeholder {
    @Id(autoincrement = true)
    private Long id;

    private int server_id;

    private String name;

    private String call_number;

    private String last_update;

    @ToMany(referencedJoinProperty = "debtor")
    private List<Transaction> transaction;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2085601650)
    private transient StakeholderDao myDao;

    @Generated(hash = 737514376)
    public Stakeholder(Long id, int server_id, String name, String call_number,
            String last_update) {
        this.id = id;
        this.server_id = server_id;
        this.name = name;
        this.call_number = call_number;
        this.last_update = last_update;
    }

    @Generated(hash = 1507418917)
    public Stakeholder() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getServer_id() {
        return this.server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCall_number() {
        return this.call_number;
    }

    public void setCall_number(String call_number) {
        this.call_number = call_number;
    }

    public String getLast_update() {
        return this.last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 797658282)
    public List<Transaction> getTransaction() {
        if (transaction == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TransactionDao targetDao = daoSession.getTransactionDao();
            List<Transaction> transactionNew = targetDao
                    ._queryStakeholder_Transaction(id);
            synchronized (this) {
                if (transaction == null) {
                    transaction = transactionNew;
                }
            }
        }
        return transaction;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 727791598)
    public synchronized void resetTransaction() {
        transaction = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1078832940)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStakeholderDao() : null;
    }
}
