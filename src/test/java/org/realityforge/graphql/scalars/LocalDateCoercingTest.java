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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class LocalDateCoercingTest
{
  @Test
  public void parseValue_String()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    assertEquals( coercing.parseValue( "2011-12-03" ), LocalDate.of( 2011, 12, 3 ) );
  }

  @Test
  public void parseValue_Number()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final LocalDate localDate = LocalDate.of( 2016, 12, 6 );
    final long epochSecond = localDate.atTime( 0, 0 ).atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    assertEquals( coercing.parseValue( epochSecond * 1000L ), localDate );
  }

  @Test
  public void parseValue_StringBadFormat()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( "2011-12-03T" ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing value '2011-12-03T'. Expected to be in the ISO local date format, such as '2011-12-03'" );
  }

  @Test
  public void parseValue_TooEarly()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( "1752-12-01" ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDate value 1752-12-01 must be between January 1, 1753 and December 31, 9999. Received: 1752-12-01" );
  }

  @Test
  public void parseValue_TooLate()
  {
    // Day after the last valid day
    final LocalDate localDate = LocalDate.of( 10000, 1, 1 );
    final long epochSecond = localDate.atTime( 0, 0 ).atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    final long epochMillis = epochSecond * 1000L;
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( epochMillis ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDate value " + epochMillis + " must be between January 1, 1753 and " +
                  "December 31, 9999. Received: +10000-01-01" );
  }

  @Test
  public void parseValue_BadType()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseValueException exception =
      expectThrows( CoercingParseValueException.class, () -> coercing.parseValue( Boolean.TRUE ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal true as it is the incorrect type. Expected a string or an integer." );
  }

  @Test
  public void parseLiteral_String()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    assertEquals( coercing.parseLiteral( new StringValue( "2011-12-03" ) ), LocalDate.of( 2011, 12, 3 ) );
  }

  @Test
  public void parseLiteral_Number()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final LocalDate localDate = LocalDate.of( 2016, 12, 6 );
    final long epochSecond = localDate.atTime( 0, 0 ).atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    assertEquals( coercing.parseLiteral( new IntValue( BigInteger.valueOf( epochSecond * 1000L ) ) ), localDate );
  }

  @Test
  public void parseLiteral_StringBadFormat()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new StringValue( "2011-12-03T" ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal '2011-12-03T'. Expected to be in the ISO local date format, such as '2011-12-03'" );
  }

  @Test
  public void parseLiteral_TooEarly()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new StringValue( "1752-12-01" ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDate literal StringValue{value='1752-12-01'} must be between January 1, 1753 and December 31, 9999. Received: 1752-12-01" );
  }

  @Test
  public void parseLiteral_TooLate()
  {
    // Day after the last valid day
    final LocalDate localDate = LocalDate.of( 10000, 1, 1 );
    final long epochSecond = localDate.atTime( 0, 0 ).atZone( ZoneOffset.systemDefault() ).toEpochSecond();
    final long epochMillis = epochSecond * 1000L;
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class,
                    () -> coercing.parseLiteral( new IntValue( BigInteger.valueOf( epochMillis ) ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "LocalDate literal IntValue{value=" + epochMillis + "} must be between January 1, 1753 " +
                  "and December 31, 9999. Received: +10000-01-01" );
  }

  @Test
  public void parseLiteral_BadType()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingParseLiteralException exception =
      expectThrows( CoercingParseLiteralException.class, () -> coercing.parseLiteral( new BooleanValue( true ) ) );
    assertEquals( exception.getErrorType(), ErrorType.ValidationError );
    assertEquals( exception.getMessage(),
                  "Error parsing literal BooleanValue{value=true} as it is the incorrect type. Expected a string or an integer." );
  }

  @Test
  public void serialize_LocalDate()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    assertEquals( coercing.serialize( LocalDate.of( 2011, 12, 3 ) ), "2011-12-03" );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void serialize_Date()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    assertEquals( coercing.serialize( new Date( 2011 - 1900, Calendar.DECEMBER, 3 ) ), "2011-12-03" );
  }

  @Test
  public void serialize_String()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    assertEquals( coercing.serialize( "2011-12-03" ), "2011-12-03" );
  }

  @Test
  public void serialize_BadString()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class,
                    () -> coercing.serialize( "2011-12-03X" ) );
    assertEquals( exception.getMessage(),
                  "Error parsing value '2011-12-03X' to serialize as a LocalDate. Expected to be in the ISO local date format, such as '2011-12-03'" );
  }

  @Test
  public void serialize_BadType()
  {
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class,
                    () -> coercing.serialize( Boolean.TRUE ) );
    assertEquals( exception.getMessage(),
                  "Failed to serialize value true as a LocalDate. Expected a type 'String', 'java.util.Date' or 'java.time.temporal.TemporalAccessor' but was 'java.lang.Boolean'." );
  }

  @Test
  public void serialize_BadTemporalType()
  {
    final Instant now = Instant.now();
    final LocalDateCoercing coercing = new LocalDateCoercing();
    final CoercingSerializeException exception =
      expectThrows( CoercingSerializeException.class, () -> coercing.serialize( now ) );
    assertEquals( exception.getMessage(), "Failed to serialize value " + now + " as a LocalDate." );
  }
}
