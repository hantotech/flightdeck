# FlightDeck - Hybrid AI Setup Guide

## Overview

FlightDeck now uses a **hybrid AI approach** that intelligently routes requests to different AI models based on:
- Task complexity
- User tier (Free, Basic, Premium)
- Cost optimization
- Performance requirements

This results in **70% cost savings** while maintaining high quality where it matters most.

---

## Supported AI Models

### Claude (Anthropic)
1. **Claude 3.5 Sonnet** - Premium accuracy
   - Cost: $3/$15 per million tokens (input/output)
   - Use: Critical evaluations, complex planning

2. **Claude 3 Haiku** - Fast and affordable
   - Cost: $0.25/$1.25 per million tokens
   - Use: Real-time ATC chat, basic guidance

### Gemini (Google)
1. **Gemini 1.5 Flash** - Very fast and cheap
   - Cost: $0.075/$0.30 per million tokens
   - **Free tier**: 1,500 requests/day
   - Use: Quick responses, free tier users

2. **Gemini 1.5 Pro** - High capability
   - Cost: $1.25/$5.0 per million tokens
   - Use: Fallback for complex tasks

---

## Smart Routing Logic

### Free Tier Users
- **All tasks** → Gemini Flash (free quota)
- Fallback: Claude Haiku (if Gemini key unavailable)
- **Monthly cost**: $0 (up to 1,500 requests/day)

### Basic Tier Users (Default)
- **High accuracy** (ATC evaluation, flight planning) → Claude Sonnet
- **Medium accuracy** (ATC chat, checklist help) → Claude Haiku
- **Low accuracy** (quick questions) → Gemini Flash
- **Monthly cost**: ~$10-15 (vs $30-50 with Sonnet only)

### Premium Tier Users
- **Critical tasks** → Claude Sonnet
- **Standard tasks** → Claude Haiku
- **Quick tasks** → Gemini Flash
- **Monthly cost**: ~$20-30

---

## Setup Instructions

### 1. Get API Keys

#### Anthropic Claude API
1. Visit https://console.anthropic.com/
2. Sign up and create an API key
3. Costs: Pay-as-you-go, ~$5 credit for new accounts

#### Google Gemini API
1. Visit https://makersuite.google.com/app/apikey
2. Sign in with Google account
3. Create API key
4. **Free tier**: 1,500 requests/day (no credit card required!)

### 2. Add API Keys to Project

Create or edit `local.properties`:

```properties
ANTHROPIC_API_KEY=sk-ant-api03-xxx
GEMINI_API_KEY=AIzaSyxxx
```

### 3. Choose User Tier

In your app initialization:

```kotlin
// In Application class or MainActivity
val userPreferences = UserPreferences.getInstance(context)

// Set user tier
lifecycleScope.launch {
    // For free users
    userPreferences.setUserTier(UserTier.FREE)

    // For basic users (default)
    userPreferences.setUserTier(UserTier.BASIC)

    // For premium subscribers
    userPreferences.setUserTier(UserTier.PREMIUM)
    userPreferences.setIsPremium(true)
}
```

---

## Usage Examples

### Basic Usage (Automatic Routing)

```kotlin
// Get the enhanced AI service
val aiService = EnhancedAIService.getInstance(UserTier.BASIC)

// Use it exactly like before - routing is automatic!
val result = aiService.generateATCResponse(
    pilotMessage = "Palo Alto Ground, Skyhawk N12345, ready to taxi",
    context = ATCContext(
        airport = "KPAO",
        scenarioType = "GROUND_CLEARANCE",
        conditions = "VFR"
    )
)

result.onSuccess { response ->
    // Response will come from Claude Haiku (fast, cheap)
    println(response)
}
```

### Evaluation (Always High Quality)

```kotlin
// Evaluation automatically uses Claude Sonnet for accuracy
val evaluation = aiService.evaluateATCCommunication(
    pilotMessage = "Palo Alto Ground, Skyhawk N12345, ready to taxi",
    expectedPhrasings = listOf("Airport Ground, Callsign, Location, Request with ATIS"),
    context = context
)

evaluation.onSuccess { eval ->
    println("Score: ${eval.score}/100")
    println("Feedback: ${eval.feedback}")
}
```

### Track Usage and Costs

```kotlin
// Get usage statistics
val stats = aiService.getUsageStats()
stats.forEach { (provider, metrics) ->
    println("$provider:")
    println("  Requests: ${metrics.totalRequests}")
    println("  Cost: ${metrics.getFormattedCost()}")
}

// Get total cost
val totalCost = aiService.getTotalEstimatedCost()
println("Total estimated cost: $%.4f".format(totalCost))

// Reset stats (e.g., at start of new billing cycle)
aiService.resetUsageStats()
```

---

## Task Routing Details

### High Accuracy Tasks (Claude Sonnet)
- `EVALUATE_ATC_COMMUNICATION` - Scoring pilot communications
- `FLIGHT_PLANNING_ADVICE` - Safety-critical route planning
- `COMPLEX_CHECKLIST_QUESTION` - Detailed technical questions
- `GENERATE_PERFORMANCE_REPORT` - Comprehensive analysis

**Why Sonnet?** These tasks require nuanced understanding and accurate feedback. Errors could affect pilot training quality.

### Medium Accuracy Tasks (Claude Haiku or Gemini Flash)
- `GENERATE_ATC_RESPONSE` - Real-time ATC chat
- `CHECKLIST_GUIDANCE` - Standard checklist explanations
- `WEATHER_INTERPRETATION` - Weather briefing help

**Why Haiku/Flash?** Good enough quality for real-time interactions, 5-40x cheaper than Sonnet.

### Low Accuracy Tasks (Gemini Flash)
- `SIMPLE_ATC_CHAT` - Basic conversation
- `BASIC_CHECKLIST_LOOKUP` - Simple lookups
- `QUICK_QUESTION` - General questions

**Why Flash?** Lightning fast, ultra-cheap, has free tier.

---

## Cost Comparison

### Scenario: Active User (100 interactions/month)

| Tier | Models Used | Est. Monthly Cost |
|------|-------------|-------------------|
| **Sonnet Only** | Claude Sonnet for everything | $30-50 |
| **Basic (Hybrid)** | Mix of Sonnet, Haiku, Flash | $10-15 |
| **Free (Flash Only)** | Gemini Flash only | $0* |

*Within free tier limits (1,500 requests/day)

### Per-Interaction Costs

| Task Type | Model Used | Cost per Request |
|-----------|------------|------------------|
| ATC Evaluation | Claude Sonnet | ~$0.015-0.025 |
| ATC Chat | Claude Haiku | ~$0.002-0.005 |
| Quick Question | Gemini Flash | ~$0.0001-0.0003 |

---

## Revenue Model Integration

### Freemium Model

```kotlin
// Free users: 10 AI interactions/day with Gemini Flash
class SubscriptionManager {
    suspend fun checkQuota(userId: String): Boolean {
        val usageToday = getUsageToday(userId)
        val userTier = userPreferences.userTier.first()

        return when (userTier) {
            UserTier.FREE -> usageToday < 10
            UserTier.BASIC, UserTier.PREMIUM -> true
        }
    }
}
```

### Pricing Tiers

**Free**
- 10 AI interactions/day
- Gemini Flash only
- Basic features
- $0/month

**Basic - $4.99/month**
- 100 AI interactions/month
- Hybrid routing (Haiku + Flash)
- All features
- ~$10-15 cost, $5 profit margin

**Premium - $9.99/month**
- Unlimited AI interactions
- Best models (Sonnet + Haiku)
- Priority support
- ~$20-30 cost for heavy users, profitable at scale

---

## Advanced Configuration

### Manual Provider Selection

```kotlin
// Force a specific provider for all tasks
val orchestrator = AIOrchestrator.getInstance(
    userTier = UserTier.PREMIUM,
    preferredProvider = "claude-haiku" // or "gemini-flash"
)

val aiService = EnhancedAIService(orchestrator)
```

### Custom Routing Logic

```kotlin
// Create custom orchestrator
class CustomAIOrchestrator : AIOrchestrator() {
    override fun selectProvider(task: AITask): AIProvider {
        // Your custom logic
        return when (timeOfDay()) {
            in 9..17 -> claudeSonnet // Peak hours: best quality
            else -> geminiFlash // Off-peak: save costs
        }
    }
}
```

---

## Testing Without API Keys

### Use Mock Providers

```kotlin
class MockAIProvider : AIProvider {
    override suspend fun generateResponse(
        systemPrompt: String,
        userMessage: String,
        temperature: Double,
        maxTokens: Int
    ): Result<AIResponse> {
        // Simulate response
        return Result.success(
            AIResponse(
                text = "Mock ATC response: Skyhawk N12345, taxi approved",
                model = "mock",
                usage = TokenUsage(10, 20)
            )
        )
    }

    override fun estimateCost(inputTokens: Int, outputTokens: Int) = 0.0
    override fun getProviderName() = "Mock"
    override fun getModelName() = "Mock Model"
}
```

---

## Migration from Old AIService

If you were using the old `AIService`, migration is seamless:

```kotlin
// Old code
val aiService = AIService.getInstance()
val result = aiService.generateATCResponse(message, context)

// New code (drop-in replacement)
val aiService = EnhancedAIService.getInstance()
val result = aiService.generateATCResponse(message, context)
// Now uses smart routing automatically!
```

---

## Monitoring & Analytics

### Track Model Performance

```kotlin
class AIAnalytics {
    fun logAIResponse(
        task: AITask,
        model: String,
        latency: Long,
        cost: Double
    ) {
        // Send to analytics
        analyticsService.track("ai_request", mapOf(
            "task" to task.name,
            "model" to model,
            "latency_ms" to latency,
            "cost" to cost
        ))
    }
}
```

### A/B Testing Models

```kotlin
// Test if users prefer Haiku or Flash for ATC chat
val testGroup = userId.hashCode() % 2
val preferredProvider = if (testGroup == 0) "claude-haiku" else "gemini-flash"

userPreferences.setPreferredProvider(preferredProvider)
```

---

## Troubleshooting

### Error: "Gemini API key not found"
```
Solution: Add GEMINI_API_KEY to local.properties
Free tier: No credit card needed at makersuite.google.com
```

### Error: "Anthropic API quota exceeded"
```
Solution:
1. Switch to FREE tier (uses Gemini)
2. Or add billing to Anthropic account
3. Or wait for quota reset
```

### Slow responses with Gemini
```
Solution: Gemini Flash is usually sub-second
Check: Internet connection, API key validity
Fallback: Will automatically use Claude Haiku
```

---

## Best Practices

1. **Start with Basic Tier** - Best balance of cost and quality
2. **Monitor costs** - Check `getTotalEstimatedCost()` regularly
3. **Use Free tier for development** - Save costs during testing
4. **Enable both APIs** - Allows fallback if one provider fails
5. **Reset stats monthly** - Track costs per billing cycle

---

## Next Steps

1. ✅ Add API keys to `local.properties`
2. ✅ Set user tier in app initialization
3. ✅ Replace `AIService` with `EnhancedAIService`
4. ✅ Test with different tiers
5. ✅ Monitor usage with `getUsageStats()`
6. ✅ Implement subscription logic
7. ✅ Ship to production!

---

## Summary

**Hybrid AI** gives you:
- ✅ **70% cost savings** vs Claude Sonnet only
- ✅ **Free tier** with Gemini Flash
- ✅ **Premium quality** where it matters
- ✅ **Automatic routing** - no code changes
- ✅ **Fallback support** - never fails
- ✅ **Usage tracking** - monitor costs
- ✅ **Scalable** - from free to enterprise

Your pilot training app now has enterprise-grade AI at a fraction of the cost! 🚀✈️
