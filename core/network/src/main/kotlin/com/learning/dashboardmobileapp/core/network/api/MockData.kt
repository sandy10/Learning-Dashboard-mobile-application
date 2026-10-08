package com.learning.dashboardmobileapp.core.network.api

import com.learning.dashboardmobileapp.core.network.model.CourseDto
import com.learning.dashboardmobileapp.core.network.model.LessonDto

object MockData {

    fun getInitialCourses(): List<CourseDto> {
        return listOf(
            CourseDto(
                id = 1L,
                title = "Python Programming",
                instructor = "John Smith",
                progress = 65,
                lessons = 20,
                category = "Computer Science",
                isCertified = true,
                totalDurationHours = 4.5,
                quizScorePercent = 92,
                lessonList = listOf(
                    LessonDto(
                        id = 101L,
                        title = "1. Introduction to Python",
                        durationMinutes = 12,
                        type = "VIDEO",
                        description = "Overview of Python syntax, interpreted nature, and environment setup with virtual environments.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 102L,
                        title = "2. Variables & Data Types",
                        durationMinutes = 18,
                        type = "CODE_LAB",
                        description = "Primitive types, type hinting, immutable vs mutable objects, strings, integers, floats, booleans.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 103L,
                        title = "3. Functions & Scope",
                        durationMinutes = 24,
                        type = "INTERACTIVE_PRACTICE",
                        description = "Master function definitions, parameters, return values, default arguments, and lambda syntax.",
                        isCompleted = false
                    ),
                    LessonDto(
                        id = 104L,
                        title = "4. OOP & Classes",
                        durationMinutes = 32,
                        type = "DEEP_DIVE",
                        description = "Classes, encapsulation, inheritance, polymorphism, dunder methods, dataclasses.",
                        isCompleted = false
                    ),
                    LessonDto(
                        id = 105L,
                        title = "5. Exception Handling",
                        durationMinutes = 15,
                        type = "PRACTICE_EXERCISE",
                        description = "Try-except blocks, custom exceptions, finally clause, context managers with 'with' keyword.",
                        isCompleted = false
                    )
                )
            ),
            CourseDto(
                id = 2L,
                title = "Generative AI",
                instructor = "Sarah Williams",
                progress = 40,
                lessons = 16,
                category = "Artificial Intelligence",
                isCertified = true,
                totalDurationHours = 6.0,
                quizScorePercent = 88,
                lessonList = listOf(
                    LessonDto(
                        id = 201L,
                        title = "1. Foundations of GenAI",
                        durationMinutes = 15,
                        type = "VIDEO",
                        description = "Transformers, self-attention mechanisms, and large language model architectures.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 202L,
                        title = "2. Prompt Engineering",
                        durationMinutes = 20,
                        type = "CODE_LAB",
                        description = "Zero-shot, few-shot, chain-of-thought, system prompts, structured outputs.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 203L,
                        title = "3. Embeddings & Vector DBs",
                        durationMinutes = 30,
                        type = "INTERACTIVE_PRACTICE",
                        description = "High-dimensional vector spaces, cosine similarity, indexing, vector databases.",
                        isCompleted = false
                    ),
                    LessonDto(
                        id = 204L,
                        title = "4. Retrieval Augmented Generation (RAG)",
                        durationMinutes = 35,
                        type = "DEEP_DIVE",
                        description = "Building naive and advanced RAG pipelines with chunking and rerankers.",
                        isCompleted = false
                    )
                )
            ),
            CourseDto(
                id = 3L,
                title = "Full Stack Development",
                instructor = "David Brown",
                progress = 25,
                lessons = 28,
                category = "Web Development",
                isCertified = true,
                totalDurationHours = 8.5,
                quizScorePercent = 85,
                lessonList = listOf(
                    LessonDto(
                        id = 301L,
                        title = "1. Modern Web Architecture",
                        durationMinutes = 14,
                        type = "VIDEO",
                        description = "Client-server communication, RESTful APIs, HTTP protocols, stateless sessions.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 302L,
                        title = "2. REST APIs & Backend Services",
                        durationMinutes = 28,
                        type = "CODE_LAB",
                        description = "Designing clean endpoints, request routing, middleware, error handling, status codes.",
                        isCompleted = true
                    ),
                    LessonDto(
                        id = 303L,
                        title = "3. State Management & Storage",
                        durationMinutes = 22,
                        type = "INTERACTIVE_PRACTICE",
                        description = "Relational and document storage, caching layers, offline persistence patterns.",
                        isCompleted = false
                    ),
                    LessonDto(
                        id = 304L,
                        title = "4. Authentication & Security",
                        durationMinutes = 30,
                        type = "DEEP_DIVE",
                        description = "JWT tokens, OAuth 2.0 flow, CSRF and XSS prevention, TLS encryption.",
                        isCompleted = false
                    )
                )
            )
        )
    }
}
