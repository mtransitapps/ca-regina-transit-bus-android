package org.mtransit.parser.ca_regina_transit_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ReginaTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new ReginaTransitBusAgencyTools().start(args);
	}

    @Override
    public @Nullable String getServiceIdCleanupRegex() {
        return "^\\d{6}-\\w{3}-";
    }

	@Override
    public @Nullable String getTripIdCleanupRegex() {
        return "\\d{6}-\\w{3}-";
    }


	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), routeLongName, getIgnoredWord());
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern EXPRESS = Pattern.compile("((^|\\W)(express)(\\W|$))", Pattern.CASE_INSENSITIVE);

	private static final String WHITMORE = "Whitmore";
	private static final Pattern WHITMORE_PARK_ = Pattern.compile("((^|\\W)(whitmore|whitmore park|whitmore pk)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String WHITMORE_PARK_REPLACEMENT = "$2" + WHITMORE + "$4";

	private static final Pattern ARCOLA_VICTORIA_DOWNTOWN_EAST_ = Pattern.compile("((^|\\W)((arcola|victoria) (downtown|east))(\\W|$))",
			Pattern.CASE_INSENSITIVE);
	private static final String ARCOLA_VICTORIA_DOWNTOWN_EAST_REPLACEMENT = "$2" + "$5 $4 " + "$6";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), tripHeadsign, getIgnoredWord());
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = EXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = WHITMORE_PARK_.matcher(tripHeadsign).replaceAll(WHITMORE_PARK_REPLACEMENT);
		tripHeadsign = ARCOLA_VICTORIA_DOWNTOWN_EAST_.matcher(tripHeadsign).replaceAll(ARCOLA_VICTORIA_DOWNTOWN_EAST_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWord() {
		return new String[]{
				"EB", "WB", "NB", "SB"
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), gStopName, getIgnoredWord());
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (StringUtils.isEmpty(gStop.getStopCode())) {
			//noinspection deprecation
			return gStop.getStopId(); // stop # used by real-time API
		}
		return super.getStopCode(gStop);
	}
}
