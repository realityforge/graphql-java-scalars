package org.realityforge.graphql.scalars;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import javax.annotation.Nonnull;

final class LocalDateTimeCoercing
  implements Coercing<LocalDateTime, String>
{
  @Nonnull
  @Override
  public String serialize( @Nonnull final Object input )
  {
    if ( input instanceof TemporalAccessor )
    {
      return serializeTemporalAccessor( input, (TemporalAccessor) input );
    }
    else if ( input instanceof Date )
    {
      return serializeDate( (Date) input );
    }
    else if ( input instanceof String )
    {
      return serializeString( (String) input );
    }
    else
    {
      final String message = "Failed to serialize value " + input + " as a LocalDateTime. Expected a " +
                             "type 'String', 'java.util.Date' or 'java.time.temporal.TemporalAccessor' " +
                             "but was '" + input.getClass().getName() + "'.";
      throw new CoercingSerializeException( message );
    }
  }

  @Nonnull
  private String serializeString( @Nonnull final String input )
  {
    return serializeTemporalAccessor( input, parseLocalDateTimeToSerialize( input ) );
  }

  @Nonnull
  private String serializeTemporalAccessor( @Nonnull final Object input,
                                            @Nonnull final TemporalAccessor temporalAccessor )
  {
    try
    {
      return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( temporalAccessor );
    }
    catch ( final DateTimeException e )
    {
      throw newCoercingSerializeException( input, e );
    }
  }

  @Nonnull
  private String serializeDate( @Nonnull final Date date )
  {
    return date.toInstant().atZone( ZoneId.systemDefault() ).format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
  }

  @Nonnull
  private LocalDateTime parseLocalDateTimeToSerialize( @Nonnull final String input )
  {
    try
    {
      return parseLocalDateTime( input );
    }
    catch ( final DateTimeParseException e )
    {
      final String message = "Error parsing value '" + input + "' to serialize as a LocalDateTime. Expected to be " +
                             "in the ISO local date-time format, such as '2011-12-03T10:15:30'";
      throw new CoercingSerializeException( message, e );
    }
  }

  @Nonnull
  private CoercingSerializeException newCoercingSerializeException( @Nonnull final Object input,
                                                                    @Nonnull final Throwable e )
  {
    return new CoercingSerializeException( "Failed to serialize value " + input + " as a LocalDateTime.", e );
  }

  @Nonnull
  @Override
  public LocalDateTime parseValue( @Nonnull final Object input )
  {
    final LocalDateTime date = doParseValue( input );
    if ( isInvalidYear( date ) )
    {
      final String message =
        "LocalDateTime value " + input + " must be between January 1, 1753 and December 31, 9999. Received: " + date;
      throw new CoercingParseValueException( message );
    }

    return date;
  }

  @Nonnull
  private LocalDateTime doParseValue( @Nonnull final Object input )
  {
    if ( input instanceof String )
    {
      try
      {
        return parseLocalDateTime( (String) input );
      }
      catch ( final Throwable t )
      {
        final String message = "Error parsing value '" + input + "'. Expected to be in the ISO date-time " +
                               "format such as '2011-12-03T10:15:30'";
        throw new CoercingParseValueException( message );
      }
    }
    else if ( input instanceof Number )
    {
      return parseLocalDateTime( ( (Number) input ).longValue() );
    }
    else
    {
      final String message = "Error parsing literal " + input + " as it is the incorrect type. " +
                             "Expected a string or an integer.";
      throw new CoercingParseValueException( message );
    }
  }

  @Nonnull
  @Override
  public LocalDateTime parseLiteral( @Nonnull final Object input )
  {
    final LocalDateTime date = doParseLiteral( input );
    if ( isInvalidYear( date ) )
    {
      final String message =
        "LocalDateTime literal " + input + " must be between January 1, 1753 and December 31, 9999. Received: " + date;
      throw new CoercingParseLiteralException( message );
    }

    return date;
  }

  @Nonnull
  private LocalDateTime doParseLiteral( @Nonnull final Object input )
  {
    if ( input instanceof StringValue )
    {
      final String value = ( (StringValue) input ).getValue();
      try
      {
        return parseLocalDateTime( value );
      }
      catch ( final Throwable t )
      {
        final String message = "Error parsing literal '" + value + "'. Expected to be in the ISO date-time " +
                               "format such as '2011-12-03T10:15:30'";
        throw new CoercingParseLiteralException( message, t );
      }
    }
    else if ( input instanceof IntValue )
    {
      return parseLocalDateTime( ( (IntValue) input ).getValue() );
    }
    else
    {
      final String message = "Error parsing literal " + input + " as it is the incorrect type. " +
                             "Expected a string or an integer.";
      throw new CoercingParseLiteralException( message );
    }
  }

  @Nonnull
  private LocalDateTime parseLocalDateTime( @Nonnull final BigInteger value )
  {
    return parseLocalDateTime( value.longValue() );
  }

  @Nonnull
  private LocalDateTime parseLocalDateTime( final long epochMilli )
  {
    return LocalDateTime.from( Instant.ofEpochMilli( epochMilli ).atZone( ZoneId.systemDefault() ) );
  }

  @Nonnull
  private LocalDateTime parseLocalDateTime( @Nonnull final String value )
  {
    return LocalDateTime.parse( value );
  }

  private boolean isInvalidYear( @Nonnull final LocalDateTime date )
  {
    final int year = date.getYear();
    return !( year >= 1753 && year <= 9999 );
  }
}
