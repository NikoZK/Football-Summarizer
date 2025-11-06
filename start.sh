#!/bin/bash

# Football Match Center - Start Script

echo "================================================"
echo "   Football Match Center - Starting Application"
echo "================================================"
echo ""

# Check if API keys are set
if [ -z "$API_KEY" ]; then
    echo "⚠️  WARNING: API_KEY (OpenAI) environment variable is not set!"
    echo "   Set it with: export API_KEY=your-openai-key"
    echo ""
    echo "❌ OpenAI API key is required for AI-powered match summaries."
    echo ""
    echo "Example:"
    echo "  export API_KEY=sk-your-openai-key-here"
    echo "  ./start.sh"
    echo ""
    exit 1
fi

echo "✅ OpenAI API key detected"
echo "✅ Using ESPN's FREE API for match data (no key needed!)"
echo ""
echo "🚀 Starting Spring Boot application..."
echo ""

./mvnw spring-boot:run
