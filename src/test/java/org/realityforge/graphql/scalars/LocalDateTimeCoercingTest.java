package org.realityforge.graphql.scalars;

import graphql.ErrorType;
import graphql.language.BooleanValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class LocalDateTimeCoercingTest
{
  @Test
  public void parseValue_String()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    assertEquals( coercing.parseValue( "2011-12-03T10:15:30" ), LocalDateTime.of( 2011, 12, 3, 10, 15, 30 ) );
  }

  @Test
  public void parseValue_Number()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final LocalDateTime localDateTime = LocalDateTime.of( 2016, 12, 6, 16, 30 );
    final long epochSecond = localDateTime.atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    assertEquals( coercing.parseValue( epochSecond * 1000L ), localDateTime );
  }

  @Test
  public void parseValue_StringBadFormat()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( "2011-12-03T10:15:30X" ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing value '2011-12-03T10:15:30X'. Expected to be in the ISO date-time format such as '2011-12-03T10:15:30'" );
  }

  @Test
  public void parseValue_TooEarly()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( "1752-12-01T23:59:59" ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDateTime value 1752-12-01T23:59:59 must be between January 1, 1753 and December 31, 9999. Received: 1752-12-01T23:59:59" );
  }

  @Test
  public void parseValue_TooLate()
  {
    // Day after the last valid day
    final LocalDateTime localDateTime = LocalDateTime.of( 10000, 1, 1, 1, 23 );
    final long epochSecond = localDateTime.atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    final long epochMillis = epochSecond * 1000L;
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( epochMillis ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDateTime value " + epochMillis + " must be between January 1, 1753 and " +
                  "December 31, 9999. Received: +10000-01-01T01:23" );
  }

  @Test
  public void parseValue_BadType()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( Boolean.TRUE ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal true as it is the incorrect type. Expected a string or an integer." );
  }

  @Test
  public void parseLiteral_String()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    assertEquals( coercing.parseLiteral( new StringValue( "2011-12-03T12:30:20" ) ),
                  LocalDateTime.of( 2011, 12, 3, 12, 30, 20 ) );
  }

  @Test
  public void parseLiteral_Number()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final LocalDateTime localDateTime = LocalDateTime.of( 2016, 12, 6, 16, 30 );
    final long epochSecond = localDateTime.atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    assertEquals( coercing.parseLiteral( new IntValue( BigInteger.valueOf( epochSecond * 1000L ) ) ), localDateTime );
  }

  @Test
  public void parseLiteral_StringBadFormat()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new StringValue( "2011-12-03T10:15:30X" ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal '2011-12-03T10:15:30X'. Expected to be in the ISO date-time format such as '2011-12-03T10:15:30'" );
  }

  @Test
  public void parseLiteral_TooEarly()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new StringValue( "1752-12-01T23:59:59" ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDateTime literal StringValue{value='1752-12-01T23:59:59'} must be between January 1, 1753 and December 31, 9999. Received: 1752-12-01T23:59:59" );
  }

  @Test
  public void parseLiteral_TooLate()
  {
    // Day after the last valid day
    final LocalDateTime localDateTime = LocalDateTime.of( 10000, 1, 1, 1, 1, 1 );
    final long epochSecond = localDateTime.atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    final long epochMillis = epochSecond * 1000L;
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new IntValue( BigInteger.valueOf( epochMillis ) ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDateTime literal IntValue{value=" + epochMillis + "} must be between January 1, 1753 " +
                  "and December 31, 9999. Received: +10000-01-01T01:01:01" );
  }

  @Test
  public void parseLiteral_BadType()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class, () -> coercing.parseLiteral( new BooleanValue( true ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal BooleanValue{value=true} as it is the incorrect type. Expected a string or an integer." );
  }

  @Test
  public void serialize_LocalDate()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    assertEquals( coercing.serialize( LocalDateTime.of( 2011, 12, 3, 23, 59, 59 ) ), "2011-12-03T23:59:59" );
  }

  @Test
  public void serialize_Date()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    assertEquals( coercing.serialize( new Date( 2011 - 1900, Calendar.DECEMBER, 3, 22, 2, 14 ) ), "2011-12-03T22:02:14" );
  }

  @Test
  public void serialize_String()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    assertEquals( coercing.serialize( "2019-11-02T12:15:37" ), "2019-11-02T12:15:37" );
  }

  @Test
  public void serialize_BadString()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class,
                    () -> coercing.serialize( "2011-12-03T10:15:30X" ) );
    assertEquals( exception.getMessage(),
                  "Error parsing value '2011-12-03T10:15:30X' to serialize as a LocalDateTime. Expected to be in the ISO local date-time format, such as '2011-12-03T10:15:30'" );
  }

  @Test
  public void serialize_BadType()
  {
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class,
                    () -> coercing.serialize( Boolean.TRUE ) );
    assertEquals( exception.getMessage(),
                  "Failed to serialize value true as a LocalDateTime. Expected a type 'String', 'java.util.Date' or 'java.time.temporal.TemporalAccessor' but was 'java.lang.Boolean'." );
  }

  @Test
  public void serialize_BadTemporalType()
  {
    final Instant now = Instant.now();
    final LocalDateTimeCoercing coercing = new LocalDateTimeCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class, () -> coercing.serialize( now ) );
    assertEquals( exception.getMessage(), "Failed to serialize value " + now + " as a LocalDateTime." );
  }
}
