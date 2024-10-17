package furhatos.app.interview.flow.main

import furhatos.app.interview.flow.ChangeMask
import furhatos.app.interview.flow.getRelativeRandomLocation
import furhatos.app.interview.setting.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.*
import furhatos.records.User

val Interview: State = state {

    onEntry {
        furhat.voice = femaleVoice
        furhat.setTexture("female")
        furhat.attend(users.random)
        furhat.say("Welcome! Let's begin your interview.")
        furhat.ask("To start, can you please tell me a bit about yourself?")
    }

    // Listening for the introduction
    onResponse {
        furhat.say("Thank you for introducing yourself. Let's dive into the technical questions.")
        goto(TechnicalQuestions)
    }

    onUserEnter {
        if (users.current != User.NOBODY) {
            furhat.glance(it)
        } else {
            furhat.attend(it)
        }
    }

    onUserLeave {
        if (users.current == it) {
            if (users.list.size > 0) {
                furhat.attend(users.random)
            } else {
                furhat.attend(interviewerLocation)
            }
        } else {
            furhat.glance(it)
        }
    }

    onTime(repeat = interval, instant = true) {
        var ampl = amplitudeUserPresent
        if (!maskChanging) {
            val loc = when {
                users.current != User.NOBODY && users.current.isVisible -> users.current.head.location
                users.list.size > 0 -> users.random.head.location
                else -> {
                    ampl = amplitudeUserAbsent
                    interviewerLocation
                }
            }
            furhat.attend(getRelativeRandomLocation(loc, ampl))
        }
    }
}

// Technical Questions Flow
val TechnicalQuestions: State = state {
    onEntry {
        furhat.ask("Let's begin with a technical question. Can you explain how you would approach solving a problem like sorting a large dataset?")
    }

    onResponse {
        furhat.say("Interesting approach. Now for the next technical question.")
        delay(200)
        furhat.ask("What is the difference between an array and a linked list, and when would you use one over the other?")
    }

    onResponse {
        furhat.say("Great answer! Let's move on to the next technical question.")
        delay(200)
        furhat.ask("How do you handle concurrency in your code? Have you worked with multi-threading before?")
    }

    onResponse {
        furhat.say("Thank you for your technical responses. Let's move on to some behavioral questions.")
        goto(BehavioralQuestions)
    }
}

// Behavioral Questions Flow
val BehavioralQuestions: State = state {
    onEntry {
        furhat.ask("Can you describe a time when you had to work in a team? What role did you play and how did you handle conflicts?")
    }

    onResponse {
        furhat.say("Thank you for your response. Let's move on to the next behavioral question.")
        delay(200)
        furhat.ask("Tell me about a time when you faced a challenging situation at work. How did you handle it?")
    }

    onResponse {
        furhat.say("Good answer! Let's proceed.")
        delay(200)
        furhat.ask("How do you deal with stress or tight deadlines in your work?")
    }

    onResponse {
        furhat.say("Thank you for your insights! This concludes the interview.")
        goto(EndInterview)
    }
}

// Ending the Interview
val EndInterview: State = state {
    onEntry {
        furhat.say("That was a great interview! We'll be in touch soon with feedback. Have a great day!")
        goto(Idle)
    }
}

// Idle state to end the conversation gracefully
val Idle = state {
    onEntry {
        furhat.say("Goodbye!")
        furhat.listen()
    }
}
