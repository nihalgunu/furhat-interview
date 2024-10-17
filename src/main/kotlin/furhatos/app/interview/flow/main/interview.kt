package furhatos.app.interview.flow.main

import furhatos.flow.kotlin.*
import furhatos.nlu.common.*
import kotlin.random.Random

val questions = mutableListOf(
    "Can you explain how you would approach solving a problem like sorting a large dataset?",
    "How do you handle concurrency in your code? Have you worked with multi-threading before?",
    "Can you describe a time when you had to work in a team? What role did you play and how did you handle conflicts?",
    "Tell me about a time when you faced a challenging situation at work. How did you handle it?",
    "How do you deal with stress or tight deadlines in your work?",
    "What programming languages are you most comfortable with, and why?",
    "Describe a project where you had to debug a complex problem. What was your approach?",
    "Tell me about a time when you had to learn a new technology quickly. How did you go about it?",
    "Have you worked in an Agile environment before? What was your role?",
    "How do you prioritize tasks when working on multiple projects at once?"
)

var questionCount = 0

val AskQuestion: State = state {
    onEntry {
        if (questionCount < 10 && questions.isNotEmpty()) {
            val randomIndex = Random.nextInt(questions.size)
            val selectedQuestion = questions[randomIndex]
            questions.removeAt(randomIndex)
            questionCount++

            furhat.ask(selectedQuestion)
        } else {
            goto(EndInterview)
        }
    }

    onResponse {
        furhat.say("Great answer!")
        reentry()
    }
}

val Interview: State = state {
    onEntry {
        furhat.setTexture("female")
        furhat.attend(users.random)
        furhat.say("Welcome! Let's begin your interview.")
        furhat.ask("To start, can you please tell me a bit about yourself?")
    }

    onResponse {
        furhat.say("Thank you for introducing yourself. Let's dive into the technical questions.")
        goto(AskQuestion)
    }
}

val EndInterview: State = state {
    onEntry {
        furhat.say("That was a great interview! We'll be in touch soon with feedback. Have a great day!")
        goto(Idle)
    }
}

val Idle: State = state {
    onEntry {
        furhat.say("Goodbye!")
        furhat.listen()
    }
}