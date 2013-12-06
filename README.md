# Penny Black

Penny Black allows you to declaratively create functions which send
HTML and text emails using [mustache](http://mustache.github.io/)
templates

## Including as a Dependency

Penny Black is comprised of:

* a core which handles templating and defining email functions
  declaratively
* email backends which use an email library to actually send the email

This separation is a little awkward but it will allow you to, for
example, write a backend which queues your emails. Right now the
backends act as adapters for

* the Clojure library [postal](https://github.com/drewr/postal)
* the Java lib [Apache Commons Email](http://commons.apache.org/proper/commons-email/)

To use Penny Black, you'll include one of the email backends in your
`project.clj` dependencies:

```clojure
;; Use the postal clojure library as your email backend
:dependencies [[com.flyingmachine/penny-black-postal "0.1.0"]]

;; Use apache commons as your email backend
:dependencies [[com.flyingmachine/penny-black-apache-commons "0.1.0"]]
```

If you're not sure which to use, try
`com.flyingmachine/penny-black-postal` and if that doesn't work try
`com.flyingmachine/penny-black-apache-commons`.

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
;; it's necessary to require both the correct backend and
;; com.flyingmachine.penny-black.core.send in order to define your senders
;; Why? Becase v0.1.0
(ns whatever
  (:require com.flyingmachine.penny-black-apache-commons
            [com.flyingmachine.penny-black.core.send :refer (defsenders)]))

;; Create the sending functions. Example of calling them below.
(defsenders
  ;; A list of args that each sending function will take
  {:args [users topic]
   :user-doseq [user users]}
  ;; senders iterate over a seq and send an email for each element
  ;; :user-doseq specifies which argument from :args corresponds to
  ;; the seq ("users") and what name to use for each element ("user").
  ;; This way you have access to element when specifying the data to
  ;; bind to your email templates.

  ;; These are defaults which you can overwrite in each sender below.
  ;; If you specify :body-data in a sender, it gets merged with the
  ;; map you supply here.
  {:to (:user/email user)
   :body-data {:topic-title (:title topic)
               :topic-id (:id topic)
               :username (:user/username user)}}

  ;; Each sending function can specify additional args. This function
  ;; will take [users topic post]
  (send-reply-notification
   [post]
   :from "custom-from@for-this-sender.com"
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

## The Name

From [wikipedia](http://en.wikipedia.org/wiki/Penny_Black):

    The Penny Black was the world's first adhesive postage stamp used
    in a public postal system. It was issued in Britain on 1 May 1840,
    for official use from 6 May of that year.
