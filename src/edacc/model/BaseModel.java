package edacc.model;

enum PersistenceState {
    SAVED, MODIFIED, NEW, DELETED
}

/**
 * abstract base class for all data classes
 * provides persistence state functionality for the database access classes
 * @author daniel
 */
abstract class BaseModel {
    protected PersistenceState persistenceState;

    public BaseModel() {
        persistenceState = PersistenceState.NEW;
    }

    protected void setNew() {
        this.persistenceState = PersistenceState.NEW;
    }

    protected void setModified() {
        this.persistenceState = PersistenceState.MODIFIED;
    }

    protected void setSaved() {
        this.persistenceState = PersistenceState.SAVED;
    }

    protected void setDeleted() {
        this.persistenceState = PersistenceState.DELETED;
    }

    protected boolean isNew() {
        return persistenceState.equals(PersistenceState.NEW);
    }

    protected boolean isModified() {
        return persistenceState.equals(PersistenceState.MODIFIED);
    }

    protected boolean isSaved() {
        return persistenceState.equals(PersistenceState.SAVED);
    }

    protected boolean isDeleted() {
        return persistenceState.equals(PersistenceState.DELETED);
    }
}
