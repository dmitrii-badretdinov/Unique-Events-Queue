package org.unique_events_queue;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public final class Record {
    private final String id;
    private final float value;
    private final Date date;

    /* The id and amount are plainly copied because they are immutable.
     * Date is copied safely to prevent its change by the malicious actors.
     * For the sake of the exercise, let's imagine that Date is needed to be stored as
     * Date instead of Instant for legacy reasons.
     *
     * It was decided to make the constructor robust when handling null inputs, but it's up for discussion.
     */
    public Record (String idInput, float amountValue, Date dateInput)
    {
        this.id = idInput == null ? "" : idInput;
        this.value = amountValue;
        this.date = (dateInput == null) ? new Date(0) : new Date(dateInput.getTime());
    }

    public Record (Record recordInput) {
        if (recordInput != null) {
            this.id = recordInput.getId();
            this.value = recordInput.getValue();
            this.date = new Date(recordInput.getDate().getTime());
        } else {
            this.id = "";
            this.value = 0;
            this.date = new Date(Instant.now().getEpochSecond());
        }
    }

    public String getId () {
        return this.id;
    }

    public float getValue() {
        return this.value;
    }

    public Date getDate() {
        return new Date (this.date.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }

        Record record = (Record) o;

        return id.equals(record.id) &&
               value == record.value &&
               date.equals(record.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, date);
    }
}
