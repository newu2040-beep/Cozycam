package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCharcoal
import com.example.ui.theme.CozyCream
import com.example.ui.theme.CozyMutedText
import com.example.ui.theme.CozyObsidian

@Composable
fun WelcomeScreen(
  onGetStarted: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            CozyObsidian,
            CozyCharcoal,
            CozyObsidian
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 520.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 28.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top header section
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          text = "CAPTURE\nREAL\nFEELINGS",
          color = CozyMutedText,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 2.5.sp,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "COZYCAM",
            color = CozyCream,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 6.sp,
            fontFamily = FontFamily.SansSerif
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "VINTAGE CAMERA",
            color = CozyMutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 4.sp
          )
        }
      }

      // Center Hero Visual
      Box(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_vintage_camera_hero),
          contentDescription = "Vintage Camera Visual",
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
          contentScale = ContentScale.Fit
        )
      }

      // Bottom Call-to-Action & Onboarding Dots
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "FILM LOOKS\nFOR A BRIGHTER TODAY",
          color = CozyMutedText,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 3.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = onGetStarted,
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag("get_started_button"),
          shape = RoundedCornerShape(29.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = CozyCream,
            contentColor = CozyObsidian
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "Get Started",
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Minimal 3-dot onboarding indicator
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(width = 16.dp, height = 4.dp)
              .clip(CircleShape)
              .background(CozyCream)
          )
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(CozyBorder)
          )
          Box(
            modifier = Modifier
              .size(4.dp)
              .clip(CircleShape)
              .background(CozyBorder)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Not now",
          color = CozyMutedText,
          fontSize = 14.sp,
          fontWeight = FontWeight.Normal,
          modifier = Modifier
            .clickable { onGetStarted() }
            .padding(8.dp)
        )
      }
    }
  }
}
