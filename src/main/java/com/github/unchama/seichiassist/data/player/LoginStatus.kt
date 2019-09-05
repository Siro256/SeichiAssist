package com.github.unchama.seichiassist.data.player

import java.time.LocalDate

data class LoginStatus(val lastLoginDate: LocalDate?, val totalLoginDay: Int = 0, val consecutiveLoginDays: Int = 0)