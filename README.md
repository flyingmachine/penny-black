# Penny Black

This library makes it a little easier to send emails. You can create
sending functions and specify text and email templates to use with
them.

## Including as a Dependency

Penny Black allows you to use different email backends. I say that as
if it's a good thing, but really I have no idea if it is. Your options
are the Clojure library [postal](https://github.com/drewr/postal) and
Apache Commons Email. Postal uses sendmail and ACE uses voodoo or
something.

If you're not sure which to use, try
`com.flyingmachine/penny-black-postal` and if that doesn't work try
`com.flyingmachine/penny-black-apache-commons`.

Include one of the following in `project.clj`:

```clojure
;; Use the postal clojure library as your email backend
:dependencies [[com.flyingmachine/penny-black-postal "0.1.0"]]

;; Use apache commons as your email backend
:dependencies [[com.flyingmachine/penny-black-apache-commons "0.1.0"]]
```

## Configuration

Penny Black uses [environ](https://github.com/weavejester/environ) to
lookup config values under `:com-flyingmachine-penny-black`. You'll
want something like the following for a leiningen profile:

```clojure
{:dev {:env {:com-flyingmachine-penny-black
             {:template-path "email-templates"
              :send-email true ;; whether to actually send; use false in development
              :host "smtp.gmail.com"
              :from-address "your-address@your-domain.com"
              :from-name "Penny Black Postal Test"
              :authentication {:username "username"
                               :password "password"}
              :use-ssl true
              :ssl-smtp-port "465"}}}}
```

## Usage

You'll need to:

1. Create senders
2. Create corresponding templates

### Define senders

`defsenders` is a macro which allows you to succinctly create
functions that:

* use the name of the sending function to find the corresponding email
  templates
* pass a map to the templates for stencil to interpolate
* iterate over a list of users, allowing you to interact with each
  individual user object

A sender will iterate over a list of users, with each individual user
accessible within the body

```clojure
;; it's necessary to require the correct backend
(ns whatever
  (:require com.flyingmachine.penny-black-apache-commons
            [com.flyingmachine.penny-black.core.send :refer (defsenders)]))

;; Create the sending functions. Example of calling them below.
(defsenders
  ;; A list of args that each sending function will take
  {:args [users topic]
   :user-doseq [user users]} ; - "user" is the name of each individual
                             ;   user object
                             ; - "users" corresponds to the virst
                             ;   value of :args above

  ;; These are defaults which you can overwrite in each sender below.
  ;; If you specify :body-data in a sender, it gets merged with the
  ;; map you supply here.
  {:from (config/setting :com.flyingmachine. :from-address)
   :to (:user/email user)
   :body-data {:topic-title (:title topic)
               :topic-id (:id topic)
               :username (:user/username user)}}

  ;; Each sending function can specify additional args. This function
  ;; will take [users topic post]
  (send-reply-notification
   [post]
   :subject (str "[Forum Site] Re: " (:title topic))
   :body-data {:content (:content post)
               :formatted-content (markdown-content post)})
  
  (send-new-topic-notification
   []
   :subject (str "[Forum Site] " (:title topic))
   :body-data {:content (:content (:first-post topic))
               :formatted-content (markdown-content (:first-post topic))}))

;; Example of calling
(let [post some-post
      topic (:topic post)
      users (db/all [:users :watching topic])]
  (send-reply-notification users topic post))
```

### Create templates

Example template for `send-new-topic-notification`. Using the above
`:template-path` configuration, this would live in
`PROJECT_ROOT/resources/email-templates/new-topic-notification.html`:

```html
<p>Hi {{username}},</p>

<p>
  There's a new topic,
  "<a href="http://gratefulplace.com/#/topics/{{topic-id}}">{{topic-title}}</a>",
  on Grateful Place. Here's the post:
</p>

<hr />
{{{formatted-content}}}
<hr />
```

### Send email

```clojure
(let [recipients (users/receiving-new-topic-notifications)
      topic (topics/create params)]
  (send-new-topic-notification recipients topic))
```

## TODO

* Handle attachments
* Figure out whether I can just use postal
* Logging/debugging facilities
* Better semantics in `defsenders`, particularly allow vars
* Not use a multimethod?
* Is it weird to have to require the backend and a core ns separately?
