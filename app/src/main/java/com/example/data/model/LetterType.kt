package com.example.data.model

enum class LetterType(val label: String, val description: String, val category: String) {
    SCHOOL_APPLICATION(
        "School Application",
        "Applications to School Principal, Class Teacher or Headmaster",
        "Education"
    ),
    LEAVE_APPLICATION(
        "Leave Application",
        "Formal sick leave, urgent leave, casual leave or vacation",
        "Personal & Work"
    ),
    BANK_LETTER(
        "Bank Formal Letter",
        "Account closure, cheque book, address change, branch manager request",
        "Banking"
    ),
    OFFICE_LETTER(
        "Office & Workplace Letter",
        "Resignation, appraisal inquiry, grievance, salary certificate",
        "Office"
    ),
    JOB_APPLICATION(
        "Job Application / Cover Letter",
        "Professional application highlighting experience and intent",
        "Career"
    ),
    GOVERNMENT_LETTER(
        "Government / Public Authority",
        "Official RTI inquiry, civic complaints, public officer petitions",
        "Government"
    ),
    COMPLAINT_LETTER(
        "Consumer & Service Complaint",
        "Defective product refund, delayed service, electricity/water dispute",
        "Complaints"
    ),
    PERMISSION_LETTER(
        "Permission & Authorization",
        "Event venue approval, field visit, official authorization",
        "Official"
    ),
    BUSINESS_LETTER(
        "Business Letter & Proposal",
        "Partnership proposal, price quotation, business inquiry",
        "Business"
    ),
    PERSONAL_LETTER(
        "Personal Letter",
        "Warm correspondence, formal invitations, notes to associates",
        "Personal"
    ),
    CUSTOM_LETTER(
        "General Standard Letter",
        "Universal formal layout with sender, date, recipient, and body",
        "General"
    );

    companion object {
        fun fromString(value: String): LetterType {
            return entries.find {
                it.name.equals(value, ignoreCase = true) || it.label.contains(value, ignoreCase = true)
            } ?: CUSTOM_LETTER
        }
    }
}

enum class LetterTone(val label: String, val description: String) {
    FORMAL("Formal", "Polite, standard executive wording"),
    RESPECTFUL("Respectful", "Humble, deferential tone suited for school or superiors"),
    URGENT("Urgent", "Direct, time-sensitive and assertive"),
    DIPLOMATIC("Diplomatic", "Measured, persuasive and objective"),
    CASUAL("Casual", "Warm and friendly tone for personal letters");

    companion object {
        fun fromString(value: String): LetterTone {
            return entries.find {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: FORMAL
        }
    }
}
