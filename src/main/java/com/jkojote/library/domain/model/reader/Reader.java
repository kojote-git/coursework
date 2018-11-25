package com.jkojote.library.domain.model.reader;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.reader.events.DownloadAddedEvent;
import com.jkojote.library.domain.model.reader.events.DownloadRemovedEvent;
import com.jkojote.library.domain.model.reader.events.RatingAddedEvent;
import com.jkojote.library.domain.model.reader.events.RatingUpdatedEvent;
import com.jkojote.library.domain.shared.Utils;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.Required;
import com.jkojote.types.Email;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Reader extends DomainEntity {

    private Email email;

    private List<Download> downloads;

    private List<Rating> ratings;

    private String encryptedPassword;

    private LocalDateTime timeRegistered;

    protected Reader(long id) {
        super(id);
    }

    public Email getEmail() {
        return email;
    }

    public List<Download> getDownloads() {
        return Collections.unmodifiableList(downloads);
    }

    public List<Rating> getRatings() {
        return Collections.unmodifiableList(ratings);
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEmail(Email email) {
        checkNotNull(email);
        this.email = email;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!hasSamePassword(oldPassword))
            return false;
        this.encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            return true;
    }

    public boolean hasSamePassword(String password) {
        return BCrypt.checkpw(password, encryptedPassword);
    }

    /**
     * Adds {@code bookInstance} to download history of this reader,
     * setting date of download to {@code LocalDate.now()} and rating to {@code rating}
     * @param bookInstance
     * @return {@code false} if history already contains download of the bookInstance
     */
    public boolean addToDownloadHistory(BookInstance bookInstance) {
        if (bookInstance == null)
            return false;
        int idx = Utils.indexOf(downloads, d -> d.getInstance().equals(bookInstance));
        if (idx == -1) {
            Download d = new Download(this, bookInstance, LocalDateTime.now());
            downloads.add(d);
            notifyAllListeners(new DownloadAddedEvent(this, d, null));
            return true;
        }
        return false;
    }

    /**
     * Removes download of the {@code bookInstance} from download history
     * @param bookInstance
     * @return {@code false} if download history doesn't contain
     *         download of the specified {@code bookInstance}
     *         or if {@code bookInstance} is {@code null}
     */
    public boolean removeFromDownloadHistory(BookInstance bookInstance) {
        if (bookInstance == null)
            return false;
        int idx = Utils.indexOf(downloads, d -> d.getInstance().equals(bookInstance));
        if (idx == -1)
            return false;
        Download d = downloads.remove(idx);
        notifyAllListeners(new DownloadRemovedEvent(this, d, null));
        return true;
    }

    public boolean addToRating(Rating rating) {
        if (rating == null || ratings.contains(rating))
            return false;
        ratings.add(rating);
        notifyAllListeners(new RatingAddedEvent(this, rating, null));
        return true;
    }

    public boolean updateRating(Rating rating) {
        if (rating == null)
            return false;
        int idx = Utils.indexOf(ratings, r -> r.equals(rating));
        if (idx == -1)
            return false;
        ratings.remove(idx);
        ratings.add(rating);
        notifyAllListeners(new RatingUpdatedEvent(this, rating, null));
        return true;
    }

    public LocalDateTime getTimeRegistered() {
        return timeRegistered;
    }

    public static final class ReaderBuilder {

        private long id;

        private Email email;

        private List<Download> downloads;

        private List<Rating> ratings;

        private String password;

        private LocalDateTime timeRegistered;

        private boolean passwordEncrypted;

        private ReaderBuilder() {
        }

        public static ReaderBuilder aReader() {
            return new ReaderBuilder();
        }

        @Required
        public ReaderBuilder withId(long id) {
            this.id = id;
            return this;
        }

        @Required
        public ReaderBuilder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public ReaderBuilder withDownloads(List<Download> downloads) {
            this.downloads = downloads;
            return this;
        }

        public ReaderBuilder withTimeRegistered(LocalDateTime time) {
            this.timeRegistered = time;
            return this;
        }

        public ReaderBuilder withPassword(String password) {
            this.password = password;
            passwordEncrypted = false;
            return this;
        }

        public ReaderBuilder withEncryptedPassword(String password) {
            passwordEncrypted = true;
            this.password = password;
            return this;
        }

        public ReaderBuilder withRatings(List<Rating> ratings) {
            this.ratings = ratings;
            return this;
        }

        public Reader build() {
            if (id <= 0)
                throw new IllegalStateException("id must be set");
            if (email == null)
                throw new IllegalStateException("email must be set");
            if (downloads == null)
                downloads = new ArrayList<>();
            if (ratings == null)
                ratings = new ArrayList<>();
            if (password == null)
                password = "";
            Reader reader = new Reader(id);
            reader.email = email;
            reader.downloads = downloads;
            reader.timeRegistered = timeRegistered;
            reader.ratings = ratings;
            if (!passwordEncrypted)
                reader.encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            else
                reader.encryptedPassword = password;
            return reader;
        }
    }
}
